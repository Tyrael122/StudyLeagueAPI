package br.studyleague.api.model.util.aggregable;

import java.util.List;

public interface Aggregable<V> {
    V addAll(List<V> aggregables);
}

