package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.model.DateRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyDataParser<T extends DailyAggregable<T>> {
    private final AggregableList<T> dailyData;

    public static <T extends DailyAggregable<T>> DailyDataParser<T> of(AggregableList<T> dailyData) {
        return new DailyDataParser<>(dailyData);
    }

    private DailyDataParser(AggregableList<T> dailyData) {
        this.dailyData = dailyData;
    }

    public T getDailyDataOrDefault(LocalDate date) {
        T dailyData = getDailyData(date);

        if (dailyData == null) {
            return getEmptyDefaultValue();
        }

        return dailyData;
    }

    public T getDailyData(LocalDate date) {
        return dailyData.stream()
                .filter(d -> date.equals(d.getDate()))
                .findFirst().orElse(null);
    }

    public T getWeeklyData(LocalDate date) {
        DateRange weekRange = DateRange.calculateWeekRange(date);
        return getWeeklyData(weekRange);
    }

    public T getWeeklyData(DateRange weekRange) {
        return sumData(weekRange.startDate(), weekRange.endDate());
    }

    public T getAllTimeData() {
        return getEmptyDefaultValue().addAll(dailyData);
    }

    private T sumData(LocalDate startDate, LocalDate endDate) {
        List<T> dataToBeAdded = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            T dailyData = getDailyData(date);

            if (dailyData != null) {
                dataToBeAdded.add(dailyData);
            }
        }

        return getEmptyDefaultValue().addAll(dataToBeAdded);
    }

    private T getEmptyDefaultValue() {
        return dailyData.getDefaultValue();
    }
}