package br.studyleague.api.model.aggregabledata;

import br.studyleague.api.model.aggregabledata.statistics.DailyStatisticsManager;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SubjectAggregableData {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private DailyStatisticsManager statisticManager = new DailyStatisticsManager();
}
