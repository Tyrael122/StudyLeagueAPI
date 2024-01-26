package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.model.util.DateRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyDataParser<T extends DailyAggregable<T>> {
    private final List<T> dailyData;
    private final Class<T> classType;

    public static <T extends DailyAggregable<T>> DailyDataParser<T> of(List<T> dailyData, Class<T> classType) {
        return new DailyDataParser<>(dailyData, classType);
    }

    private DailyDataParser(List<T> dailyData, Class<T> classType) {
        this.dailyData = dailyData;
        this.classType = classType;
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
        try {
            return classType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create empty default value for " + classType.getName());
        }
    }
}