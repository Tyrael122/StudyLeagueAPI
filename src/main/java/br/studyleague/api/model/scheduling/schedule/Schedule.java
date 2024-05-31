package br.studyleague.api.model.scheduling.schedule;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.aggregabledata.StudentAggregableData;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.scheduling.StudySchedulingMethod;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.GradeCalculator;
import enums.StatisticType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Data
@Entity
public class Schedule implements StudySchedulingMethod {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StudyDay> days = new ArrayList<>();

    @Override
    public void syncGradesByDate(Student student, LocalDate date) {
        StudentAggregableData aggregableData = student.getAggregableData();

        float dailyGrade = calculateDailyGrade(date);
        aggregableData.setDailyGrade(date, dailyGrade);

        DateRange weekRange = DateRange.calculateWeeklyRange(date);
        float weeklyGrade = calculateWeeklyGrade(weekRange, getAllSubjects());
        aggregableData.setWeeklyGrade(weekRange, weeklyGrade);
    }

    @Override
    public int calculateHoursGoalsCompleted(Student student, LocalDate date) {
        Map<Subject, Float> subjectsToStudy = getSubjectsWithDailyHourTarget(date.getDayOfWeek());
        int hoursGoalsCompleted = 0;
        for (Subject subject : subjectsToStudy.keySet()) {
            float hoursStudied = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date).getValue(StatisticType.HOURS);
            float hoursGoal = subjectsToStudy.get(subject);

            if (hoursStudied >= hoursGoal) {
                hoursGoalsCompleted++;
            }
        }

        return hoursGoalsCompleted;
    }

    public void syncSubjectHourGoalsWithSchedule(List<Subject> studentSubjects) {
        Map<Subject, Float> subjectHours = new HashMap<>();

        for (StudyDay studyDay : days) {
            for (ScheduleEntry entry : studyDay.getEntries()) {
                syncEntryWithStudentSubject(entry, studentSubjects);

                preventDatabaseConflits(entry);

                Subject subject = entry.getSubject();
                float duration = entry.getDuration();

                subjectHours.put(subject, subjectHours.getOrDefault(subject, 0F) + duration);
            }
        }

        for (Subject subject : subjectHours.keySet()) {
            subject.getGoals().setWeeklyGoal(StatisticType.HOURS, subjectHours.get(subject));
        }
    }

    public List<Subject> getAllSubjects() {
        Set<Subject> subjects = new HashSet<>();
        for (StudyDay studyDay : days) {
            for (ScheduleEntry entry : studyDay.getEntries()) {
                subjects.add(entry.getSubject());
            }
        }

        return subjects.stream().toList();
    }

    @Override
    public List<Subject> getSubjects(DayOfWeek dayOfWeek) {
        return getSubjectsWithDailyHourTarget(dayOfWeek).keySet().stream().toList();
    }

    public Map<Subject, Float> getSubjectsWithDailyHourTarget(DayOfWeek dayOfWeek) {
        StudyDay studyDay = getStudyDay(dayOfWeek);

        Map<Subject, Float> subjects = new HashMap<>();
        for (ScheduleEntry entry : studyDay.getEntries()) {
            Subject subject = entry.getSubject();
            float duration = entry.getDuration();

            subjects.put(subject, subjects.getOrDefault(subject, 0F) + duration);
        }

        return subjects;
    }

    public StudyDay getStudyDay(DayOfWeek dayOfWeek) {
        StudyDay day = days.stream()
                .filter(studyDay -> studyDay.getDayOfWeek().equals(dayOfWeek))
                .findFirst()
                .orElse(null);

        if (day == null) {
            day = new StudyDay();
            day.setDayOfWeek(dayOfWeek);

            days.add(day);
        }

        return day;
    }

    private float calculateDailyGrade(LocalDate date) {
        float hoursToStudy = 0;
        float hoursStudied = 0;

        Map<Subject, Float> subjectWithHoursToStudyToday = getSubjectsWithDailyHourTarget(date.getDayOfWeek());

        for (Subject subject : subjectWithHoursToStudyToday.keySet()) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);

            hoursToStudy += subjectWithHoursToStudyToday.get(subject);
            hoursStudied += subjectStatistic.getValue(StatisticType.HOURS);
        }

        return GradeCalculator.calculateDailyGrade(hoursStudied, hoursToStudy);
    }

    private float calculateWeeklyGrade(DateRange weekRange, List<Subject> subjects) {
        float hoursStudiedAverage = calculateHoursStudiedGrade(weekRange, subjects);
        float doneQuestionsAverage = GradeCalculator.calculateQuestionsGrade(weekRange, subjects);
        float reviewsDoneAverage = GradeCalculator.calculateReviewsGrade(weekRange, subjects);

        return GradeCalculator.calculateWeeklyGrade(doneQuestionsAverage, hoursStudiedAverage, reviewsDoneAverage);
    }

    private float calculateHoursStudiedGrade(DateRange weekRange, List<Subject> subjects) {
        float hoursToStudy = 0;
        float hoursStudied = 0;

        for (Subject subject : subjects) {
            Statistic subjectStatistics = Statistic.parse(subject.getDailyStatistics()).getWeeklyData(weekRange);

            hoursToStudy += subject.getGoals().getWeeklyGoal(StatisticType.HOURS);
            hoursStudied += subjectStatistics.getValue(StatisticType.HOURS);
        }

        return GradeCalculator.ceilGrade(hoursStudied, hoursToStudy);
    }

    private static void syncEntryWithStudentSubject(ScheduleEntry entry, List<Subject> studentSubjects) {
        long subjectId = entry.getSubject().getId();

        Subject subject = studentSubjects.stream()
                .filter(s -> s.getId().equals(subjectId))
                .findFirst()
                .orElse(null);

        entry.setSubject(subject);
    }

    private void preventDatabaseConflits(ScheduleEntry entry) {
        entry.setId(null);
    }
}
