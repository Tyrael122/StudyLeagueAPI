package br.studyleague.api.model.student.schedule;

import br.studyleague.api.model.subject.Subject;
import enums.StatisticType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StudyDay> days = new ArrayList<>();

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
