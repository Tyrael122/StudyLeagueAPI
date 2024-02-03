package br.studyleague.api.model.util;

import br.studyleague.api.model.subject.Subject;
import enums.StatisticType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GradeCalculatorTest {

    private final LocalDate currentDate = LocalDate.now();

    @Test
    void calculateWeeklyGradeWithExtraPoint() {
        List<Subject> subjects = mockSubjects();

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertEquals(5.75, grade);
    }

    @Test
    void calculateWeeklyGradeWhenNoGoalsSet() {
        // TODO: Assert the grade calculated is zero.
    }

    @Test
    void calculateDailyGrade() {

    }

    @Test
    void calculateDailyGradesTakesIntoConsiderationOnlyNeededStats() {
        // TODO: Assert the daily grade takes into consideration only the hours and the number of hour goals completed.
        //  For that, calculate the goal normally, then update a stats that shouldn't be considered and ask to calculate again.
    }

    private List<Subject> mockSubjects() {
        List<Subject> subjects = new ArrayList<>();

        Subject subject1 = new Subject();
        subject1.setName("Física");
        subject1.getGoals().setWeeklyGoal(StatisticType.HOURS, 10);
        subject1.getGoals().setWeeklyGoal(StatisticType.QUESTIONS, 10);
        subject1.getGoals().setWeeklyGoal(StatisticType.REVIEWS, 10);

        subject1.getStatisticManager().setStatisticValue(currentDate, StatisticType.HOURS, 97);
        subject1.getStatisticManager().setStatisticValue(currentDate, StatisticType.QUESTIONS, 50);
        subject1.getStatisticManager().setStatisticValue(currentDate, StatisticType.REVIEWS, 100);

        subjects.add(subject1);

        Subject subject2 = new Subject();
        subject2.setName("Artes");
        subject2.getGoals().setWeeklyGoal(StatisticType.HOURS, 20);
        subject2.getGoals().setWeeklyGoal(StatisticType.QUESTIONS, 20);
        subject2.getGoals().setWeeklyGoal(StatisticType.REVIEWS, 20);

        subject2.getStatisticManager().setStatisticValue(currentDate, StatisticType.HOURS, 1);
        subject2.getStatisticManager().setStatisticValue(currentDate, StatisticType.QUESTIONS, 5);
        subject2.getStatisticManager().setStatisticValue(currentDate, StatisticType.REVIEWS, 0);

        subjects.add(subject2);

        return subjects;
    }
}