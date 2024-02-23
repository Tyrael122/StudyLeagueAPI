package br.studyleague.api.controller.util.datetime;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateTimeGenerator {
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

    public static LocalDate now() {
        return LocalDate.now(DEFAULT_ZONE);
    }
}
