package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.model.util.DateRange;

import java.time.LocalDate;
import java.util.List;

public class RawDataParser<T extends Aggregable<T>> {
    private final List<T> aggregableList;
    private final Class<T> classType;

    public static <T extends Aggregable<T>> RawDataParser<T> of(List<T> dailyData, Class<T> classType) {
        return new RawDataParser<>(dailyData, classType);
    }

    private RawDataParser(List<T> aggregableList, Class<T> classType) {
        this.aggregableList = aggregableList;
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
        DateRange dayRange = new DateRange(date, date);

        return aggregableList.stream()
                .filter(d -> dayRange.contains(d.getRange()))
                .findFirst().orElse(null);
    }

    public T getWeeklyData(LocalDate date) {
        DateRange weekRange = DateRange.calculateWeekRange(date);
        return getWeeklyData(weekRange);
    }

    public T getWeeklyData(DateRange weekRange) {
        return sumDataWithinRange(weekRange);
    }

    public T getMonthlyData(LocalDate currentDate) {
        DateRange monthRange = DateRange.calculateMonthRangeWithWeekOffset(currentDate);

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
        try {
            return classType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create empty default value for " + classType.getName());
        }
    }
}