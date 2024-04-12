package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.controller.util.datetime.DateRange;

import java.time.LocalDate;
import java.util.List;

public class RawDataParser<T extends Aggregable<T>> {
    private final List<T> aggregableList;
    private final T defaultEmptyValue;

    public static <T extends Aggregable<T>> RawDataParser<T> of(List<T> dailyData, T defaultEmptyValue) {
        return new RawDataParser<>(dailyData, defaultEmptyValue);
    }

    private RawDataParser(List<T> aggregableList, T defaultEmptyValue) {
        this.aggregableList = aggregableList;
        this.defaultEmptyValue = defaultEmptyValue;
    }

    public T getDailyDataOrDefault(LocalDate date) {
        T dailyData = getDailyData(date);

        if (dailyData == null) {
            return getEmptyDefaultValue();
        }

        return dailyData;
    }

    public T getDailyData(LocalDate date) {
        DateRange dayRange = new DateRange(date, date);

        return aggregableList.stream()
                .filter(d -> dayRange.contains(d.getRange()))
                .findFirst().orElse(null);
    }

    public T getWeeklyData(LocalDate date) {
        DateRange weekRange = DateRange.calculateWeeklyRange(date);
        return getWeeklyData(weekRange);
    }

    public T getWeeklyData(DateRange weekRange) {
        return sumDataWithinRange(weekRange);
    }

    public T getMonthlyData(LocalDate currentDate) {
        DateRange monthRange = DateRange.calculateMonthlyRangeWithWholeWeeks(currentDate);
        return sumDataWithinRange(monthRange);
    }

    public T getAllTimeData() {
        return getEmptyDefaultValue().addAll(aggregableList);
    }

    private T sumDataWithinRange(DateRange range) {
        List<T> dataWithinRange = aggregableList.stream()
                .filter(d -> range.contains(d.getRange())).toList();

        return getEmptyDefaultValue().addAll(dataWithinRange);
    }

    private T getEmptyDefaultValue() {
        return defaultEmptyValue;
    }
}