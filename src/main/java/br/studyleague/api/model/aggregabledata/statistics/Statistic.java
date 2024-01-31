package br.studyleague.api.model.aggregabledata.statistics;

import br.studyleague.api.model.util.DateRange;
import br.studyleague.api.model.util.aggregable.Aggregable;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import dtos.statistic.ReadStatisticDTO;
import enums.StatisticType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Statistic implements Aggregable<Statistic> {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    private LocalDate date;

    private float hoursStudied = 0;
    private int questionsAnswered = 0;
    private int reviewsMade = 0;

    public static RawDataParser<Statistic> parse(List<Statistic> dailyStatistics) {
        return RawDataParser.of(dailyStatistics, Statistic.class);
    }

    public static ReadStatisticDTO toReadDto(Statistic statistic) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(statistic, ReadStatisticDTO.class);
    }

    public float getValue(StatisticType statisticType) {
        return switch (statisticType) {
            case HOURS -> hoursStudied;
            case QUESTIONS -> questionsAnswered;
            case REVIEWS -> reviewsMade;
            default -> throw new IllegalArgumentException("Not supported statistic type");
        };
    }

    public void setValue(StatisticType statisticType, float value) {
        switch (statisticType) {
            case HOURS -> hoursStudied = value;
            case QUESTIONS -> questionsAnswered = (int) value;
            case REVIEWS -> reviewsMade = (int) value;
            default -> throw new IllegalArgumentException("Not supported statistic type");
        }
    }

    @Override
    public Statistic addAll(List<Statistic> dailyStatistics) {
        Statistic statistic = new Statistic();

        for (Statistic dailyStatistic : dailyStatistics) {
            statistic.addWith(dailyStatistic);
        }

        return statistic;
    }

    private void addWith(Statistic statistic) {
        increaseStatisticValue(StatisticType.HOURS, statistic.getValue(StatisticType.HOURS));
        increaseStatisticValue(StatisticType.QUESTIONS, statistic.getValue(StatisticType.QUESTIONS));
        increaseStatisticValue(StatisticType.REVIEWS, statistic.getValue(StatisticType.REVIEWS));
    }

    private void increaseStatisticValue(StatisticType statisticType, float value) {
        setValue(statisticType, getValue(statisticType) + value);
    }

    @Override
    public DateRange getRange() {
        return new DateRange(date, date);
    }
}
