package br.studyleague.api.controller.util.datetime;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeUtilsTest {

    @Test
    public void testStudentTimezoneOffsettedDate_DefaultOffset() {
        LocalDateTime inputDateTime = LocalDateTime.of(2024, 2, 25, 12, 0);
        int offset = -3;

        LocalDate result = DateTimeUtils.studentTimezoneOffsettedDate(inputDateTime, offset).toLocalDate();

        assertEquals(LocalDateTime.of(2024, 2, 25, 9, 0).toLocalDate(), result);
    }

    @Test
    public void testStudentTimezoneOffsettedDate_CustomOffset() {
        LocalDateTime inputDateTime = LocalDateTime.of(2024, 2, 25, 12, 0);
        int offset = -5;

        LocalDateTime result = DateTimeUtils.studentTimezoneOffsettedDate(inputDateTime, offset);

        assertEquals(LocalDateTime.of(2024, 2, 25, 7, 0), result);
    }

    @Test
    public void returnsPreviousDayCorrectly() {
        LocalDateTime inputDateTime = LocalDateTime.of(2024, 2, 25, 2, 59);
        int offset = -3;

        LocalDateTime result = DateTimeUtils.studentTimezoneOffsettedDate(inputDateTime, offset);

        assertEquals(LocalDateTime.of(2024, 2, 24, 23, 59), result);
    }
}
