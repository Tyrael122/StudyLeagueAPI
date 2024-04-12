package br.studyleague.api.model.goals;

import enums.StatisticType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Goal {

    @Id
    @GeneratedValue
    private Long id;

    private StatisticType statisticType;

    private float target;
}
