package br.studyleague.api.controller.util.datetime;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record DateRange(LocalDate startDate, LocalDate endDate) {
    public static DateRange calculateWeekRange(LocalDate date) {
        var start = getStartOfWeek(date);
        var end = start.plusDays(6);

        return new DateRange(start, end);
    }

    public static DateRange calculateMonthRangeWithWeekOffset(LocalDate currentDate) {
        var currentStartOfWeek = getStartOfWeek(currentDate);

        var endOfMonth = currentStartOfWeek.plusDays(currentStartOfWeek.lengthOfMonth() - 1);

        var startOfLastWeek = endOfMonth.minusDays(endOfMonth.getDayOfWeek().getValue() - 1);
        var endOfLastWeek = startOfLastWeek.plusDays(6);

        return new DateRange(currentStartOfWeek, endOfLastWeek);
    }

    public List<LocalDate> getDaysInRange() {
        List<LocalDate> days = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            days.add(date);
        }

        return days;
    }

    public List<DateRange> getWeeksInRange() {
        List<DateRange> weeks = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(7)) {
            weeks.add(calculateWeekRange(date));
        }

        return weeks;
    }

    public boolean contains(DateRange range) {
        return !startDate.isAfter(range.startDate) && !endDate.isBefore(range.endDate);
    }

    @NotNull
    private static LocalDate getStartOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
}