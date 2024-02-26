package br.studyleague.api.controller.util.datetime;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
public class DateTimeUtils {
    private static final int STUDENT_DEFAULT_TIMEZONE_OFFSET = -3;

    public static LocalDate timezoneOffsettedNowDate() {
        return studentTimezoneOffsettedDate(LocalDate.now(), STUDENT_DEFAULT_TIMEZONE_OFFSET).toLocalDate();
    }

    public static LocalDateTime timezoneOffsettedNow() {
        return studentTimezoneOffsettedDate(LocalDateTime.now(), STUDENT_DEFAULT_TIMEZONE_OFFSET);
    }

    public static LocalDate studentTimezoneOffsettedDate(LocalDate date) {
        return studentTimezoneOffsettedDate(date, STUDENT_DEFAULT_TIMEZONE_OFFSET).toLocalDate();
    }

    public static LocalDateTime studentTimezoneOffsettedDate(LocalDate localDate, int offset) {
        return studentTimezoneOffsettedDate(LocalDateTime.of(localDate, LocalTime.now()), offset);
    }

    public static LocalDateTime studentTimezoneOffsettedDate(LocalDateTime localDateTime, int offset) {
        LocalDateTime calculatedDate = localDateTime.plusHours(offset);

        log.info("Calculated date: " + calculatedDate);

        return calculatedDate;
    }
}
