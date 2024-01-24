package br.studyleague.api.model.util.aggregable;

import java.util.List;

public interface AggregableList<T> extends List<T> {
    T getDefaultValue();
}
