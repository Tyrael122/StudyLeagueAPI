package br.studyleague.api.model.util.aggregable;

import java.util.ArrayList;

public class AggregableArrayList<T> extends ArrayList<T> implements AggregableList<T> {
    private final T defaultValue;

    public AggregableArrayList(T defaultValue) {
        super();

        this.defaultValue = defaultValue;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
}
