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
    void calculateRealCaseWeeklyGrade() {
        List<Subject> subjects = mockRealCaseSubjects();

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertEquals(1.54495370388031, grade);
    }

    @Test
    void calculateMaxWeeklyGradeWithExtraPointWhenReachingAllGoals() {
        List<Subject> subjects = mockPerfectSubjects();

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertEquals(10, grade);
    }

    @Test
    void calculateWeeklyGradeWhenNoGoalsSet() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Física", List.of(0F, 0F, 0F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Artes", List.of(0F, 0F, 0F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Direito", List.of(0F, 0F, 0F), List.of(0F, 0F, 0F)));

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertEquals(0, grade);
    }

    @Test
    void calculateWeeklyGradeWithExtraPoint() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Economia", List.of(3F, 100F, 100F), List.of(3F, 110F, 110F)));
        subjects.add(mockSubject("Biologia", List.of(3F, 100F, 100F), List.of(3F, 110F, 110F)));
        subjects.add(mockSubject("Geologia", List.of(3F, 100F, 100F), List.of(3F, 80F, 80F)));

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertEquals(10, grade);
    }

    @Test
    void weeklyGradeLimitsAmountOfExtraPoints() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Economia", List.of(3F, 100F, 100F), List.of(3F, 1000F, 1000F)));
        subjects.add(mockSubject("Biologia", List.of(3F, 100F, 100F), List.of(3F, 1000F, 1000F)));
        subjects.add(mockSubject("Geologia", List.of(3F, 100F, 100F), List.of(3F, 80F, 80F)));

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertEquals(10, grade);
    }

    @Test
    void weeklyGradeGivesProportionalPoints() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Economia", List.of(3F, 100F, 100F), List.of(3F, 105F, 105F)));
        subjects.add(mockSubject("Biologia", List.of(3F, 100F, 100F), List.of(3F, 105F, 105F)));
        subjects.add(mockSubject("Geologia", List.of(3F, 100F, 100F), List.of(3F, 80F, 80F)));

        DateRange weekRange = DateRange.calculateWeekRange(currentDate);
        float grade = GradeCalculator.calculateWeeklyGrade(weekRange, subjects);

        assertTrue(grade > 9.5 && grade < 10);
    }

    @Test
    void calculateDailyGrade() {
        Map<Subject, Float> subjectWithHoursToStudyToday = Map.of(
                mockSubject("Física", List.of(0F, 0F, 0F), List.of(1F, 0F, 0F)), 2F,
                mockSubject("Artes", List.of(0F, 0F, 0F), List.of(1F, 0F, 0F)), 2F
        );

        float grade = GradeCalculator.calculateDailyGrade(currentDate, subjectWithHoursToStudyToday);
        assertEquals(5, grade);
    }

    @Test
    void calculateDailyGradeWhenGoalsAreEmpty() {
        Map<Subject, Float> subjectWithHoursToStudyToday = Map.of(
                mockSubject("Física", List.of(0F, 0F, 0F), List.of(0F, 0F, 0F)), 0F,
                mockSubject("Artes", List.of(0F, 0F, 0F), List.of(0F, 0F, 0F)), 0F
        );

        float grade = GradeCalculator.calculateDailyGrade(currentDate, subjectWithHoursToStudyToday);
        assertEquals(0, grade);
    }

    @Test
    void calculateDailyGradeWhenGoalsAreMet() {
        Map<Subject, Float> subjectWithHoursToStudyToday = Map.of(
                mockSubject("Física", List.of(0F, 0F, 0F), List.of(2F, 0F, 0F)), 2F,
                mockSubject("Artes", List.of(0F, 0F, 0F), List.of(2F, 0F, 0F)), 2F
        );

        float grade = GradeCalculator.calculateDailyGrade(currentDate, subjectWithHoursToStudyToday);
        assertEquals(10, grade);
    }

    @Test
    void calculateDailyGradeWhenGoalsAreSurpassed() {
        Map<Subject, Float> subjectWithHoursToStudyToday = Map.of(
                mockSubject("Física", List.of(0F, 0F, 0F), List.of(10F, 0F, 0F)), 2F,
                mockSubject("Artes", List.of(0F, 0F, 0F), List.of(10F, 0F, 0F)), 2F
        );

        float grade = GradeCalculator.calculateDailyGrade(currentDate, subjectWithHoursToStudyToday);
        assertEquals(10, grade);
    }

    private List<Subject> mockPerfectSubjects() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Física", List.of(3F, 60F, 160F), List.of(3F, 60F, 160F)));
        subjects.add(mockSubject("Artes", List.of(3F, 60F, 160F), List.of(3F, 60F, 160F)));

        return subjects;
    }

    private List<Subject> mockRealCaseSubjects() {
        List<Subject> subjects = new ArrayList<>();

        subjects.add(mockSubject("Direito Constitucional", List.of(3.0F, 60F, 160F), List.of(1.0F, 21F, 75F)));
        subjects.add(mockSubject("Direito Administrativo", List.of(3.0F, 60F, 160F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Auditoria", List.of(3.0F, 60F, 200F), List.of(1.0F, 28F, 97F)));
        subjects.add(mockSubject("Contabilidade", List.of(4.0F, 80F, 300F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Direito Tributário", List.of(3.0F, 60F, 200F), List.of(1.0F, 13F, 100F)));
        subjects.add(mockSubject("Português", List.of(5.0F, 80F, 200F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Matemática", List.of(3.0F, 40F, 60F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Legislação Tributária", List.of(4.0F, 60F, 200F), List.of(1.0F, 7F, 88F)));
        subjects.add(mockSubject("Direito Penal, Civil e Empresarial", List.of(6.0F, 90F, 300F), List.of(0F, 0F, 0F)));
        subjects.add(mockSubject("Informática", List.of(4.0F, 60F, 200F), List.of(1.0F, 0F, 96F)));
        subjects.add(mockSubject("Anki diário", List.of(7.0F, 0F, 0F), List.of(0F, 0F, 0F)));

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
            subject.getStatisticManager().setStatisticValue(currentDate, entry.getKey(), entry.getValue());
        }

        return subject;
    }
}