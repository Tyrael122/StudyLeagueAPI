package br.studyleague.api.controller.util.datetime;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public record DateRange(LocalDate startDate, LocalDate endDate) {
    private static final DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final DayOfWeek LAST_DAY_OF_WEEK = DayOfWeek.SUNDAY;

    public static DateRange calculateWeeklyRange(LocalDate date) {
        var start = calculateStartOfWeek(date);
        var end = calculateEndOfWeek(start);

        return new DateRange(start, end);
    }

    public static DateRange calculateMonthlyRangeWithWholeWeeks(LocalDate date) {
        var initialDate = calculateStartDateForMonthlyRange(date);

        var endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        return new DateRange(initialDate, calculateEndOfWeek(endOfMonth));
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

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.with(TemporalAdjusters.next(FIRST_DAY_OF_WEEK))) {
            weeks.add(calculateWeeklyRange(date));
        }

        return weeks;
    }

    public boolean contains(DateRange range) {
        return !startDate.isAfter(range.startDate) && !endDate.isBefore(range.endDate);
    }

    @NotNull
    private static LocalDate calculateStartOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(FIRST_DAY_OF_WEEK));
    }

    @NotNull
    private static LocalDate calculateEndOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(LAST_DAY_OF_WEEK));
    }

    @NotNull
    private static LocalDate calculateStartDateForMonthlyRange(LocalDate date) {
        var startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        var initialDate = calculateStartOfWeek(startOfMonth);

        if (initialDate.isBefore(startOfMonth)) {
            initialDate = calculateEndOfWeek(startOfMonth).plusDays(1);
        }

        return initialDate;
    }
}