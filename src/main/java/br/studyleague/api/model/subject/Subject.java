package br.studyleague.api.model.subject;

import br.studyleague.api.model.aggregabledata.SubjectAggregableData;
import br.studyleague.api.model.aggregabledata.statistics.Statistic;
import br.studyleague.api.model.goals.SubjectGoals;
import dtos.SubjectDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Delegate;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @OneToOne(cascade = CascadeType.ALL)
    private SubjectAggregableData aggregableData = new SubjectAggregableData();

    public List<Statistic> getDailyStatistics() {
        return aggregableData.getStatisticManager().getRawStatistics();
    }

    public static Statistic sumSubjectStatistics(LocalDate date, List<Subject> subjects) {
        List<Statistic> subjectStatistics = new ArrayList<>();
        for (Subject subject : subjects) {
            Statistic subjectStatistic = Statistic.parse(subject.getDailyStatistics()).getDailyDataOrDefault(date);
            subjectStatistics.add(subjectStatistic);
        }

        return new Statistic().addAll(subjectStatistics);
    }
}