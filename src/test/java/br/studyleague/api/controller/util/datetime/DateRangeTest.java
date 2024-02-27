package br.studyleague.api.controller.util.datetime;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeTest {

    @Test
    void calculateMonthRangeInBeginningOfMonth() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 1));

        assertEquals(LocalDate.of(2024, 1, 29), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 3), dateRange.endDate());
    }

    @Test
    void calculateMonthRangeInMiddleOfTheMonth() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 14));

        assertEquals(LocalDate.of(2024, 1, 29), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 3), dateRange.endDate());
    }

    @Test
    void monthRangeIsTheSameRangeInTheEndOfTheMonth() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 2, 29));

        assertEquals(LocalDate.of(2024, 1, 29), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 3), dateRange.endDate());
    }

    @Test
    void ensureDateOverlapping() {
        DateRange dateRange = DateRange.calculateMonthlyRangeWithWholeWeeks(LocalDate.of(2024, 3, 1));

        assertEquals(LocalDate.of(2024, 2, 26), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 31), dateRange.endDate());
    }
}