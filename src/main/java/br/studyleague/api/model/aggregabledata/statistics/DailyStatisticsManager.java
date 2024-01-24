package br.studyleague.api.model.aggregabledata.statistics;

import br.studyleague.api.model.util.aggregable.AggregableArrayList;
import br.studyleague.api.model.util.aggregable.AggregableList;
import br.studyleague.api.model.util.aggregable.DailyDataParser;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
public class DailyStatisticsManager {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    private AggregableList<Statistic> rawStatistics = new AggregableArrayList<>(new Statistic());

    public void setStatisticValue(LocalDate date, StatisticType statisticType, float value) {
        Statistic statistic = DailyDataParser.of(rawStatistics).getDailyData(date);
        if (statistic == null) {
            statistic = new Statistic();
            statistic.setDate(date);
            rawStatistics.add(statistic);
        }

        statistic.setValue(statisticType, value);
    }

    public void setStatisticValue(LocalDate date, Statistic newStatistic) {
        for (int i = 0; i < rawStatistics.size(); i++) {
            Statistic currentStatistic = rawStatistics.get(i);
            if (date.equals(currentStatistic.getDate())) {
                rawStatistics.set(i, newStatistic);
                return;
            }
        }

        rawStatistics.add(newStatistic);
    }
}
