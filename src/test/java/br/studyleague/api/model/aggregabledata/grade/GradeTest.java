package br.studyleague.api.model.aggregabledata.grade;

import br.studyleague.api.controller.util.datetime.DateRange;
import br.studyleague.api.model.util.aggregable.RawDataParser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradeTest {
    private List<Float> weeklyGrades = new ArrayList<>();
    private final LocalDate defaultInitialDefault = LocalDate.of(2024, 2, 1);

    @Test
    void calculateMonthlyGrade() {
        weeklyGrades = List.of(10F, 8F, 9F, 7F, 5F);

        float monthlyGrade = calculateMonthlyGrade(defaultInitialDefault);

        assertEquals(7.8, monthlyGrade, 0.0001);
    }

    @Test
    void calculatePerfectMonthlyGrade() {
        weeklyGrades = List.of(10F, 10F, 10F, 10F, 10F);

        float monthlyGrade = calculateMonthlyGrade(defaultInitialDefault);

        assertEquals(10, monthlyGrade);
    }

    @Test
    void calculateMonthlyGradeWithSingleWeek() {
        weeklyGrades = List.of(8F);

        LocalDate initialDate = LocalDate.of(2024, 2, 1);
        float monthlyGrade = calculateMonthlyGrade(initialDate);

        assertEquals(1.6, monthlyGrade, 0.0001);
    }

    @Test
    void calculateMonthlyGradeWithNoData() {
        weeklyGrades = new ArrayList<>();

        LocalDate initialDate = LocalDate.of(2024, 2, 1);
        float monthlyGrade = calculateMonthlyGrade(initialDate);

        assertEquals(0, monthlyGrade);
    }

    @Test
    void calculateMonthlyGradeWithNegativeGrades() {
        weeklyGrades = List.of(10F, -8F, 9F, 7F, 5F);

        LocalDate initialDate = LocalDate.of(2024, 2, 1);
        float monthlyGrade = calculateMonthlyGrade(initialDate);

        assertEquals(4.6, monthlyGrade, 0.0001);
    }

    private float calculateMonthlyGrade(LocalDate initialDate) {
        List<DateRange> weeks = DateRange.calculateMonthlyRangeWithWholeWeeks(initialDate).getWeeksInRange();

        List<Grade> grades = new ArrayList<>();

        for (int i = 0; i < weeks.size(); i++) {
            Grade grade = new Grade();

            grade.setStartDate(weeks.get(i).startDate());
            grade.setEndDate(weeks.get(i).endDate());

            grade.setGrade(calculateWeeklyGrade(i));

            grades.add(grade);
        }

        RawDataParser<Grade> weeklyGradeParser = Grade.parse(grades);

        return Grade.calculateMonthlyGrade(initialDate, weeklyGradeParser);
    }

    private float calculateWeeklyGrade(int i) {
        if (weeklyGrades.size() <= i) return 0;
        return weeklyGrades.get(i);
    }
}