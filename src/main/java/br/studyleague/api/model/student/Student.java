package br.studyleague.api.model.student;

import br.studyleague.api.model.aggregabledata.StudentAggregableData;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.student.schedule.Schedule;
import br.studyleague.api.model.student.schedule.StudyDay;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.DateRange;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Delegate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String name = "";
    private String studyArea = "";
    private String goal = "";

    @OneToMany(cascade = CascadeType.ALL)
    private List<Subject> subjects = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Schedule schedule = new Schedule();

    @Delegate
    @OneToOne(cascade = CascadeType.ALL)
    private StudentAggregableData aggregableData = new StudentAggregableData();

    public List<Statistic> getDailyStatistics() {
        return aggregableData.getStatisticManager().getRawStatistics();
    }

    public void syncStatisticsWithSubjects(LocalDate updatedDate) {
        List<Subject> todaySubjects = schedule.getSubjects(updatedDate.getDayOfWeek());

        Statistic newDailyStatistic = sumSubjectStatistics(updatedDate, todaySubjects);
        newDailyStatistic.setDate(updatedDate);

        aggregableData.getStatisticManager().setStatisticValue(updatedDate, newDailyStatistic);
        syncGradesByDate(updatedDate);
    }

    public void syncGradesByDate(LocalDate updatedDate) {
        Map<Subject, Float> subjectWithHoursToStudyToday = schedule.getSubjectsWithDailyHourTarget(updatedDate.getDayOfWeek());
        aggregableData.syncDailyGrade(updatedDate, subjectWithHoursToStudyToday);

        DateRange weekRange = DateRange.calculateWeekRange(updatedDate);
        aggregableData.syncWeeklyGrade(weekRange, subjects);
    }

    public Subject findSubjectById(Long subjectId) {
        return subjects.stream()
                .filter(subject -> subject.getId().equals(subjectId))
                .findFirst()
                .orElseThrow();
    }

    private static Statistic sumSubjectStatistics(LocalDate date, List<Subject> subjects) {
        List<Statistic> subjectStatistics = new ArrayList<>();
        for (Subject subject : subjects) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);
            subjectStatistics.add(subjectStatistic);
        }

        return new Statistic().addAll(subjectStatistics);
    }
}
