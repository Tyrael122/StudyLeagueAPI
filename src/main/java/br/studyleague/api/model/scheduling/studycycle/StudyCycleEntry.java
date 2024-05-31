package br.studyleague.api.model.scheduling.studycycle;

import br.studyleague.api.model.subject.Subject;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StudyCycleEntry {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Subject subject;
    private int durationInMinutes;
}
