package br.studyleague.api.model.aggregabledata.grade;

import br.studyleague.api.model.util.aggregable.DailyAggregable;
import br.studyleague.api.model.util.aggregable.DailyDataParser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Grade implements DailyAggregable<Grade> {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;
    private float grade = 0;

    public static DailyDataParser<Grade> parse(List<Grade> dailyGrades) {
        return DailyDataParser.of(dailyGrades, Grade.class);
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
}
