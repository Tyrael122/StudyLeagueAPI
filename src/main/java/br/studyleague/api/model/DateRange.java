package br.studyleague.api.model;

import java.time.LocalDate;

public record DateRange(LocalDate startDate, LocalDate endDate) {
    public static DateRange calculateWeekRange(LocalDate date) {
        var start = date.minusDays(date.getDayOfWeek().getValue() - 1);
        var end = start.plusDays(6);

        return new DateRange(start, end);
    }

    public static DateRange calculateMonthRange(LocalDate currentDate) {
        var start = currentDate.minusDays(currentDate.getDayOfMonth() - 1);
        var end = start.plusDays(start.lengthOfMonth() - 1);

        return new DateRange(start, end);
    }

    public boolean contains(DateRange range) {
        return !startDate.isAfter(range.startDate) && !endDate.isBefore(range.endDate);
    }
}