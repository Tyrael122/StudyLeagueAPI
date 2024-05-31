package br.studyleague.api.model.util;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.subject.Subject;
import enums.StatisticType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeCalculatorTest {

    private final LocalDate currentDate = LocalDate.now();

    @Test
    void calculateWeeklyGrade() {
        float grade = GradeCalculator.calculateWeeklyGrade(1F, 1F, 1F);

        assertEquals(10F, grade);
    }

    @Test
    void calculateWeeklyGradeWithZeroedGrade() {
        float grade = GradeCalculator.calculateWeeklyGrade(1F, 0F, 1F);

        assertEquals(6.666F, grade, 0.1);
    }

    @Test
    void calculateWeeklyGradeWithAllZeroedGrades() {
        float grade = GradeCalculator.calculateWeeklyGrade(0F, 0F, 0F);

        assertEquals(0F, grade);
    }

    @Test
    void calculateWeeklyGradeWithExtraPoint() {
        float grade = GradeCalculator.calculateWeeklyGrade(1F, 0.9F, 1.1F);

        assertEquals(10F, grade);
    }

    @Test
    void calculatePerfectStatisticsGrade() {
        DateRange weekRange = DateRange.calculateWeeklyRange(currentDate);
        List<Subject> subjects = mockPerfectSubjects();

        float grade = GradeCalculator.calculateQuestionsGrade(weekRange, subjects);
        assertEquals(1F, grade);

        grade = GradeCalculator.calculateReviewsGrade(weekRange, subjects);
        assertEquals(1F, grade);
    }

    @Test
    void calculateSurpassedStatisticsGrade() {
        DateRange weekRange = DateRange.calculateWeeklyRange(currentDate);

        List<Subject> subjects = new ArrayList<>();
        subjects.add(mockSubject("Geografia", List.of(3F, 60F, 160F), List.of(3F, 66F, 176F)));

        float grade = GradeCalculator.calculateQuestionsGrade(weekRange, subjects);
        assertEquals(1.1F, grade);

        grade = GradeCalculator.calculateReviewsGrade(weekRange, subjects);
        assertEquals(1.1F, grade);
    }

    @Test
    void calculateDailyGrade() {
        float grade = GradeCalculator.calculateDailyGrade(1F, 1F);

        assertEquals(10F, grade);
    }

    @Test
    void calculateDailyGradeWithZeroedGoal() {
        float grade = GradeCalculator.calculateDailyGrade(1F, 0F);

        assertEquals(0F, grade);
    }

    @Test
    void calculateZeroedDailyGrade() {
        float grade = GradeCalculator.calculateDailyGrade(0F, 1F);

        assertEquals(0F, grade);
    }

    @Test
    void calculateDailyGradeHalved() {
        float grade = GradeCalculator.calculateDailyGrade(0.5F, 1F);

        assertEquals(5F, grade);
    }

    private List<Subject> mockPerfectSubjects() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Física", List.of(3F, 60F, 160F), List.of(3F, 60F, 160F)));
        subjects.add(mockSubject("Artes", List.of(3F, 60F, 160F), List.of(3F, 60F, 160F)));

        return subjects;
    }

    private Subject mockSubject(String name, List<Float> goals, List<Float> stats) {
        Map<StatisticType, Float> goalsMap = Map.of(
                StatisticType.HOURS, goals.get(0),
                StatisticType.QUESTIONS, goals.get(1),
                StatisticType.REVIEWS, goals.get(2)
        );

        Map<StatisticType, Float> statsMap = Map.of(
                StatisticType.HOURS, stats.get(0),
                StatisticType.QUESTIONS, stats.get(1),
                StatisticType.REVIEWS, stats.get(2)
        );

        Subject mockedSubject = mockSubject(goalsMap, statsMap);
        mockedSubject.setName(name);

        return mockedSubject;
    }

    private Subject mockSubject(Map<StatisticType, Float> goals, Map<StatisticType, Float> stats) {
        Subject subject = new Subject();

        for (Map.Entry<StatisticType, Float> entry : goals.entrySet()) {
            subject.getGoals().setWeeklyGoal(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<StatisticType, Float> entry : stats.entrySet()) {
            subject.getAggregableData().getStatisticManager().setStatisticValue(currentDate, entry.getKey(), entry.getValue());
        }

        return subject;
    }
}