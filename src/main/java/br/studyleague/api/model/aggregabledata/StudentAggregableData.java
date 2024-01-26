package br.studyleague.api.model.aggregabledata;

import br.studyleague.api.model.aggregabledata.grade.Grade;
import br.studyleague.api.model.aggregabledata.grade.WeeklyGrade;
import br.studyleague.api.model.aggregabledata.statistics.DailyStatisticsManager;
import br.studyleague.api.model.student.schedule.ScheduleEntry;
import br.studyleague.api.model.student.schedule.StudyDay;
import br.studyleague.api.model.subject.Subject;
import br.studyleague.api.model.util.DateRange;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.studyleague.api.model.util.GradeCalculator.calculateDailyGrade;
import static br.studyleague.api.model.util.GradeCalculator.calculateWeeklyGrade;

@Data
@Entity
public class StudentAggregableData {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private DailyStatisticsManager statisticManager = new DailyStatisticsManager();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Grade> dailyGrades = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<WeeklyGrade> weeklyGrades = new ArrayList<>();

    public void syncDailyGrade(LocalDate date, StudyDay studyDay) {
        float studentDailyGrade = calculateDailyGrade(date, studyDay.getSchedule());

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
