package br.studyleague.api.controller.util.datetime;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateRangeTest {

    @Test
    void calculateMonthRangeInBeginningOfMonth() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 1));

        assertFebuaryIsCorrect(dateRange);
    }

    @Test
    void calculateMonthRangeInMiddleOfTheMonth() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 14));

        assertFebuaryIsCorrect(dateRange);
    }

    @Test
    void monthRangeIsTheSameRangeInTheEndOfTheMonth() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 29));

        assertFebuaryIsCorrect(dateRange);
    }

    @Test
    void calculateMonthlyRangeInAnotherMonth() {
        DateRange secondDateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 3, 1));

        assertEquals(LocalDate.of(2024, 3, 4), secondDateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 31), secondDateRange.endDate());
    }

    @Test
    void ensureRangesDontOverlap() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 1));

        DateRange secondDateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 3, 1));

        assertTrue(dateRange.endDate().isBefore(secondDateRange.startDate()));
    }

    @Test
    void ensureRangesDontOverlapWithOtherMonths() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 4, 1));

        assertEquals(LocalDate.of(2024, 4, 1), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 5, 5), dateRange.endDate());

        DateRange secondDateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 5, 1));
        assertEquals(LocalDate.of(2024, 5, 6), secondDateRange.startDate());
        assertEquals(LocalDate.of(2024, 6, 2), secondDateRange.endDate());

        assertTrue(dateRange.endDate().isBefore(secondDateRange.startDate()));
    }

    private void assertFebuaryIsCorrect(DateRange dateRange) {
        assertEquals(LocalDate.of(2024, 2, 5), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 3), dateRange.endDate());
    }
}