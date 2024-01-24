package br.studyleague.api.model.aggregabledata.grade;

import br.studyleague.api.model.util.DateRange;
import br.studyleague.api.model.util.aggregable.RangedAggregable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class WeeklyGrade implements RangedAggregable<WeeklyGrade> {

    @Id
    @GeneratedValue
    private Long id;

    private float grade = 0;

    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    public WeeklyGrade addAll(List<WeeklyGrade> aggregables) {
        float gradesSum = 0;
        for (WeeklyGrade grade : aggregables) {
            gradesSum += grade.getGrade();
        }

        WeeklyGrade newGrade = new WeeklyGrade();
        newGrade.setGrade(gradesSum / aggregables.size());

        return newGrade;
    }

    @Override
    public DateRange getRange() {
        return new DateRange(startDate, endDate);
    }
}
