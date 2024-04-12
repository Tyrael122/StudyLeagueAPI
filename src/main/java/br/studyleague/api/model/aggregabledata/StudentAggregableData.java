package br.studyleague.api.model.aggregabledata;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.aggregabledata.grade.Grade;
import br.studyleague.api.model.aggregabledata.statistics.DailyStatisticsManager;
import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private List<Grade> weeklyGrades = new ArrayList<>();

    public void syncDailyGrade(LocalDate date, Map<Subject, Float> subjectWithHoursToStudyToday) {
        float studentDailyGrade = calculateDailyGrade(date, subjectWithHoursToStudyToday);
        DateRange dateRange = new DateRange(date, date);

        setGrade(dailyGrades, dateRange, studentDailyGrade);
    }

    public void syncWeeklyGrade(DateRange weekRange, List<Subject> subjects) {
        float studentWeeklyGrade = calculateWeeklyGrade(weekRange, subjects);

        setGrade(weeklyGrades, weekRange, studentWeeklyGrade);
    }

    private void setGrade(List<Grade> grades, DateRange dateRange, float newGrade) {
        Grade newDailyGrade = new Grade();
        newDailyGrade.setStartDate(dateRange.startDate());
        newDailyGrade.setEndDate(dateRange.endDate());
        newDailyGrade.setGrade(newGrade);

        updateGradesList(grades, dateRange, newDailyGrade);
    }

    private void updateGradesList(List<Grade> grades, DateRange dateRange, Grade newGrade) {
        for (int i = 0; i < grades.size(); i++) {
            Grade currentGrade = grades.get(i);
            if (dateRange.equals(currentGrade.getRange())) {
                grades.set(i, newGrade);
                return;
            }
        }

        grades.add(newGrade);
    }
}
