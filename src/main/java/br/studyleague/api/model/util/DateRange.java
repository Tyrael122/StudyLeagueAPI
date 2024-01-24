package br.studyleague.api.model.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<LocalDate> getDaysInRange() {
        List<LocalDate> days = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            days.add(date);
        }

        return days;
    }

    public boolean contains(DateRange range) {
        return !startDate.isAfter(range.startDate) && !endDate.isBefore(range.endDate);
    }
}