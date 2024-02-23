package br.studyleague.api.model.aggregabledata.grade;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.util.aggregable.Aggregable;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Grade implements Aggregable<Grade> {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    private float grade = 0;

    public static RawDataParser<Grade> parse(List<Grade> grades) {
        return RawDataParser.of(grades, new Grade());
    }

    public static float calculateMonthlyGrade(LocalDate date, RawDataParser<Grade> weeklyGradeParser) {
        float weeklyGradesSum = weeklyGradeParser.getMonthlyData(date).getGrade();
        int numberOfWeeks = DateRange.calculateMonthRangeWithWeekOffset(date).getWeeksInRange().size();

        return weeklyGradesSum / numberOfWeeks;
    }

    @Override
    public Grade addAll(List<Grade> aggregables) {
        Grade grade = new Grade();
        if (aggregables.isEmpty()) {
            return grade;
        }

        float sum = 0;
        for (Grade aggregable : aggregables) {
            sum += aggregable.getGrade();
        }

        grade.setGrade(sum);
        return grade;
    }

    @Override
    public DateRange getRange() {
        return new DateRange(startDate, endDate);
    }
}
