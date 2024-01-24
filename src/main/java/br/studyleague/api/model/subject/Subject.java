package br.studyleague.api.model.subject;

import br.studyleague.api.model.goals.SubjectGoals;
import br.studyleague.api.model.util.aggregable.AggregableList;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.aggregabledata.SubjectAggregableData;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.experimental.Delegate;

@Data
@Entity
public class Subject {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToOne
    private SubjectGoals goals = new SubjectGoals();

    @OneToOne
    @Delegate
    private SubjectAggregableData aggregableData = new SubjectAggregableData();

    public AggregableList<Statistic> getDailyStatistics() {
        return aggregableData.getStatisticManager().getRawStatistics();
    }
}

