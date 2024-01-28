package br.studyleague.api.model.aggregabledata.grade;

import br.studyleague.api.model.util.DateRange;
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

    public static RawDataParser<Grade> parse(List<Grade> dailyGrades) {
        return RawDataParser.of(dailyGrades, Grade.class);
    }

    @Override
    public Grade addAll(List<Grade> aggregables) {
        Grade dailyGrade = new Grade();

        float sum = 0;
        for (Grade aggregable : aggregables) {
            sum += aggregable.getGrade();
        }

        dailyGrade.setGrade(sum / aggregables.size());

        return dailyGrade;
    }

    @Override
    public DateRange getRange() {
        return new DateRange(startDate, endDate);
    }
}
