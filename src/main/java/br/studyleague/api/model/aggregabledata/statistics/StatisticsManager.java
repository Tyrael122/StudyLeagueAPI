package br.studyleague.api.model.aggregabledata.statistics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class StatisticsManager {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    private List<Statistic> dailyStatistics = new ArrayList<>();
}
