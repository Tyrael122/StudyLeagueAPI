package br.studyleague.api.model.statistics;

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
