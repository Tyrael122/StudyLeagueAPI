package br.studyleague.api.model.subject;

import br.studyleague.api.model.aggregabledata.SubjectAggregableData;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.goals.SubjectGoals;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Delegate;

import java.util.List;

@Data
@Entity
public class Subject {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private SubjectGoals goals = new SubjectGoals();

    @Delegate
    @OneToOne(cascade = CascadeType.ALL)
    private SubjectAggregableData aggregableData = new SubjectAggregableData();

    public List<Statistic> getDailyStatistics() {
        return aggregableData.getStatisticManager().getRawStatistics();
    }
}

