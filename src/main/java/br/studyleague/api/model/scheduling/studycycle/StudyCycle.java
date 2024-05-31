package br.studyleague.api.model.scheduling.studycycle;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.aggregabledata.StudentAggregableData;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.scheduling.StudySchedulingMethod;
import br.studyleague.api.model.student.Student;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.GradeCalculator;
import enums.StatisticType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class StudyCycle implements StudySchedulingMethod {

    @Id
    @GeneratedValue
    private long id;

    @OrderBy
    @OneToMany(cascade = CascadeType.ALL)
    private List<StudyCycleEntry> entries = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int currentEntryIndex = 0;

    private int weeklyMinutesToStudy = 0;

    @Override
    public List<Subject> getSubjects(DayOfWeek dayOfWeek) {
        return getAllSubjects();
    }

    @Override
    public void syncGradesByDate(Student student, LocalDate date) {
        StudentAggregableData aggregableData = student.getAggregableData();

        float dailyGrade = calculateDailyGrade(date);
        aggregableData.setDailyGrade(date, dailyGrade);

        DateRange weekRange = DateRange.calculateWeeklyRange(date);
        float weeklyGrade = calculateWeeklyGrade(weekRange, getAllSubjects());
        aggregableData.setWeeklyGrade(weekRange, weeklyGrade);
    }

    private float calculateDailyGrade(LocalDate date) {
        float hoursStudied = 0;

        for (Subject subject : getAllSubjects()) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);

            hoursStudied += subjectStatistic.getValue(StatisticType.HOURS);
        }

        return GradeCalculator.calculateDailyGrade(hoursStudied, calculateHoursToStudy());
    }

    private float calculateWeeklyGrade(DateRange weekRange, List<Subject> subjects) {
        float hoursStudiedAverage = calculateHoursStudiedGrade(weekRange, subjects);
        float doneQuestionsAverage = GradeCalculator.calculateQuestionsGrade(weekRange, subjects);
        float reviewsDoneAverage = GradeCalculator.calculateReviewsGrade(weekRange, subjects);

        return GradeCalculator.calculateWeeklyGrade(doneQuestionsAverage, hoursStudiedAverage, reviewsDoneAverage);
    }

    private float calculateHoursStudiedGrade(DateRange weekRange, List<Subject> subjects) {
        float hoursStudied = 0;

        for (Subject subject : subjects) {
            Statistic subjectStatistics = Statistic.parse(subject.getDailyStatistics()).getWeeklyData(weekRange);

            hoursStudied += subjectStatistics.getValue(StatisticType.HOURS);
        }

        return GradeCalculator.ceilGrade(hoursStudied, calculateHoursToStudy());
    }

    @Override
    public int calculateHoursGoalsCompleted(Student student, LocalDate date) {
        return 0;
    }

    private List<Subject> getAllSubjects() {
        return entries.stream().map(StudyCycleEntry::getSubject).toList();
    }

    public StudyCycleEntry getCurrentEntry() {
        if (entries.isEmpty()) {
            return null;
        }

        return entries.get(currentEntryIndex);
    }

    public void nextEntry() {
        currentEntryIndex = (currentEntryIndex + 1) % entries.size();
    }

    public boolean isAtStart() {
        return currentEntryIndex == 0;
    }

    public void setEntries(List<StudyCycleEntry> entries) {
        this.entries = entries;

        currentEntryIndex = 0;
    }

    private float calculateHoursToStudy() {
        return (float) weeklyMinutesToStudy / 60;
    }
}
