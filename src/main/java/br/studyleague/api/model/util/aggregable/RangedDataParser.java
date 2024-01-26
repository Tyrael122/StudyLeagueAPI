package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.model.util.DateRange;

import java.time.LocalDate;
import java.util.List;

public class RangedDataParser<T extends RangedAggregable<T>> {
    private final List<T> aggregableList;

    private RangedDataParser(List<T> aggregableList) {
        this.aggregableList = aggregableList;
    }

    public static <T extends RangedAggregable<T>> RangedDataParser<T> of(List<T> aggregableList) {
        return new RangedDataParser<>(aggregableList);
    }

    public T getWeeklyData(LocalDate date) {
        DateRange weekRange = DateRange.calculateWeekRange(date);

        return sumDataWithinRange(weekRange);
    }

    public T getMonthlyData(LocalDate currentDate) {
        DateRange monthRange = DateRange.calculateMonthRange(currentDate);

        return sumDataWithinRange(monthRange);
    }

    private T sumDataWithinRange(DateRange range) {
        List<T> dataWithinRange = aggregableList.stream()
                .filter(d -> range.contains(d.getRange())).toList();

        return getEmptyDefaultValue().addAll(dataWithinRange);
    }

    private T getEmptyDefaultValue() {
        throw new RuntimeException("Empty default value not implemented");
    }
}
