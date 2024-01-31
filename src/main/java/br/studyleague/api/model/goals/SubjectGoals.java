package br.studyleague.api.model.goals;

import enums.StatisticType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class SubjectGoals {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Goal> weeklyGoals = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Goal> allTimeGoals = new ArrayList<>();

    public void setWeeklyGoal(StatisticType type, float value) {
        getGoalValue(weeklyGoals, type).setValue(value);
    }

    public float getWeeklyGoal(StatisticType type) {
        return getGoalValue(weeklyGoals, type).getValue();
    }

    public float getAllTimeGoal(StatisticType type) {
        return getGoalValue(allTimeGoals, type).getValue();
    }
    public void setAllTimeGoal(StatisticType type, float value) {
        getGoalValue(allTimeGoals, type).setValue(value);
    }

    private Goal getGoalValue(List<Goal> goals, StatisticType type) {
        Goal matchedGoal = goals.stream()
                .filter(goal -> goal.getStatisticType().equals(type))
                .findFirst()
                .orElse(null);

        if (matchedGoal == null) {
            matchedGoal = new Goal();
            matchedGoal.setStatisticType(type);
            goals.add(matchedGoal);
        }

        return matchedGoal;
    }
}
