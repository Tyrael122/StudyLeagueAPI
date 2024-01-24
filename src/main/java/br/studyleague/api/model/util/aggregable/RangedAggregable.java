package br.studyleague.api.model.util.aggregable;

import br.studyleague.api.model.DateRange;

public interface RangedAggregable<V> extends Aggregable<V> {
    DateRange getRange();
}
