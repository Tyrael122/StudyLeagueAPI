package br.studyleague.api.model.student.schedule;

import br.studyleague.api.model.aggregabledata.statistics.StatisticType;
import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.*;

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
            for (ScheduleEntry entry : studyDay.getSchedule()) {
                syncEntryWithStudentSubject(entry, studentSubjects);

                Subject subject = entry.getSubject();
                float duration = entry.getDuration();

                subjectHours.put(subject, subjectHours.getOrDefault(subject, 0F) + duration);
            }
        }

        for (Subject subject : subjectHours.keySet()) {
            subject.getGoals().setWeeklyGoal(StatisticType.HOURS, subjectHours.get(subject));
        }
    }

    private static void syncEntryWithStudentSubject(ScheduleEntry entry, List<Subject> studentSubjects) {
        long subjectId = entry.getSubject().getId();

        Subject subject = studentSubjects.stream()
                .filter(s -> s.getId().equals(subjectId))
                .findFirst()
                .orElse(null);

        entry.setSubject(subject);
    }

    public List<Subject> getSubjects(DayOfWeek dayOfWeek) {
        StudyDay studyDay = getStudyDay(dayOfWeek);

        Set<Subject> subjects = new HashSet<>();
        for (ScheduleEntry entry : studyDay.getSchedule()) {
            subjects.add(entry.getSubject());
        }

        return subjects.stream().toList();
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
}
