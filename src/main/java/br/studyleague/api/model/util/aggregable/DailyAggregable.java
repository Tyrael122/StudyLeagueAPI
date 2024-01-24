package br.studyleague.api.model.util.aggregable;

import java.time.LocalDate;

public interface DailyAggregable<V> extends Aggregable<V> {
    LocalDate getDate();
}
