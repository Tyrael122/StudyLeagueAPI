package br.studyleague.api.model.aggregabledata.statistics;

import br.studyleague.dtos.enums.StatisticType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
public class DailyStatisticsManager {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Statistic> rawStatistics = new ArrayList<>();

    public void setStatisticValue(LocalDate date, StatisticType statisticType, float value) {
        Statistic statistic = Statistic.parse(rawStatistics).getDailyData(date);
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
