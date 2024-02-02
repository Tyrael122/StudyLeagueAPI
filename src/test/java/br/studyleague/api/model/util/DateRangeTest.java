package br.studyleague.api.model.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateRangeTest {

    @Test
    void calculateMonthRange() {
        DateRange dateRange = DateRange.calculateMonthRangeWithWeekOffset(LocalDate.of(2024, 2, 1));

        assertEquals(LocalDate.of(2024, 1, 29), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 3), dateRange.endDate());
    }

    @Test
    void calculateMonthRangeWithNoLastWeekOffset() {
        DateRange dateRange = DateRange.calculateMonthRangeWithWeekOffset(LocalDate.of(2024, 3, 1));

        assertEquals(LocalDate.of(2024, 2, 26), dateRange.startDate());
        assertEquals(LocalDate.of(2024, 3, 31), dateRange.endDate());
    }
}