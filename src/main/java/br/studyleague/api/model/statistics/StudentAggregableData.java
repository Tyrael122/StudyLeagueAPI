package br.studyleague.api.model.statistics;

import br.studyleague.api.model.DateRange;
import br.studyleague.api.model.student.StudyDay;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.aggregable.AggregableArrayList;
import br.studyleague.api.model.util.aggregable.AggregableList;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import static br.studyleague.api.model.util.GradeCalculator.calculateDailyGrade;
import static br.studyleague.api.model.util.GradeCalculator.calculateWeeklyGrade;

@Data
@Entity
public class StudentAggregableData {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private DailyStatisticsManager statisticManager = new DailyStatisticsManager();

    @OneToMany
    private AggregableList<Grade> dailyGrades = new AggregableArrayList<>(new Grade());

    @OneToMany
    private AggregableList<WeeklyGrade> weeklyGrades = new AggregableArrayList<>(new WeeklyGrade());

    public void syncDailyGrade(LocalDate date, StudyDay studyDay) {
        float studentDailyGrade = calculateDailyGrade(date, studyDay);

        Grade newGrade = new Grade();
        newGrade.setDate(date);
        newGrade.setGrade(studentDailyGrade);

        setGrade(date, newGrade);
    }

    public void syncWeeklyGrade(DateRange weekRange, List<Subject> subjects) {
        float studentWeeklyGrade = calculateWeeklyGrade(weekRange, subjects);

        WeeklyGrade newWeeklyGrade = new WeeklyGrade();
        newWeeklyGrade.setStartDate(weekRange.startDate());
        newWeeklyGrade.setEndDate(weekRange.endDate());
        newWeeklyGrade.setGrade(studentWeeklyGrade);

        setWeeklyGrade(weekRange, newWeeklyGrade);
    }

    private void setGrade(LocalDate date, Grade newGrade) {
        for (int i = 0; i < dailyGrades.size(); i++) {
            Grade currentStatistic = dailyGrades.get(i);
            if (date.equals(currentStatistic.getDate())) {
                dailyGrades.set(i, newGrade);
                return;
            }
        }

        dailyGrades.add(newGrade);
    }

    private void setWeeklyGrade(DateRange dateRange, WeeklyGrade newWeeklyGrade) {
        for (int i = 0; i < weeklyGrades.size(); i++) {
            WeeklyGrade currentWeeklyGrade = weeklyGrades.get(i);
            if (dateRange.equals(currentWeeklyGrade.getRange())) {
                weeklyGrades.set(i, newWeeklyGrade);
                return;
            }
        }

        weeklyGrades.add(newWeeklyGrade);
    }
}
