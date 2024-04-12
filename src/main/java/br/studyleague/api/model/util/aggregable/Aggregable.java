package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.controller.util.datetime.DateRange;

import java.util.List;

public interface Aggregable<V> {
    V addAll(List<V> aggregables);
    DateRange getRange();
}

