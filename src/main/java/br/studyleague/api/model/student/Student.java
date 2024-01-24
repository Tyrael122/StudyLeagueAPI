package br.studyleague.api.model.student;

import br.studyleague.api.model.DateRange;
import br.studyleague.api.model.util.aggregable.AggregableList;
import br.studyleague.api.model.statistics.Statistic;
import br.studyleague.api.model.statistics.StudentAggregableData;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.DailyDataParser;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Delegate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String studyArea;
    private String goal;

    @OneToMany
    private List<Subject> subjects = new ArrayList<>();

    @OneToOne
    private Schedule schedule = new Schedule();

    @OneToOne
    @Delegate
    private StudentAggregableData aggregableData = new StudentAggregableData();

    public AggregableList<Statistic> getDailyStatistics() {
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
        aggregableData.syncDailyGrade(updatedDate, schedule.getStudyDay(updatedDate.getDayOfWeek()));

        DateRange weekRange = DateRange.calculateWeekRange(updatedDate);
        aggregableData.syncWeeklyGrade(weekRange, subjects);
    }

    private static Statistic sumSubjectStatistics(LocalDate date, List<Subject> subjects) {
        List<Statistic> subjectStatistics = new ArrayList<>();
        for (Subject subject : subjects) {
            Statistic subjectStatistic = DailyDataParser.of(subject.getDailyStatistics()).getDailyDataOrDefault(date);
            subjectStatistics.add(subjectStatistic);
        }

        return new Statistic().addAll(subjectStatistics);
    }
}
