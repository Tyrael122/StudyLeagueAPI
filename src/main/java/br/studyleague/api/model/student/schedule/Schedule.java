package br.studyleague.api.model.student.schedule;

import br.studyleague.api.model.aggregabledata.statistics.StatisticType;
import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    private List<StudyDay> days = new ArrayList<>();

    public void addScheduleEntry(ScheduleEntry entry, DayOfWeek day) {
        StudyDay studyDay = getStudyDay(day);

        studyDay.getSchedule().add(entry);

        float goalValue = entry.getSubject().getGoals().getWeeklyGoal(StatisticType.HOURS);
        entry.getSubject().getGoals().setWeeklyGoal(StatisticType.HOURS, entry.getDuration() + goalValue);
    }

    public List<Subject> getSubjects(DayOfWeek dayOfWeek) {
        StudyDay studyDay = getStudyDay(dayOfWeek);

        List<Subject> subjects = new ArrayList<>();
        for (ScheduleEntry entry : studyDay.getSchedule()) {
            subjects.add(entry.getSubject());
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
}
