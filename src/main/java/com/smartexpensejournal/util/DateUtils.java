package com.smartexpensejournal.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtils() {
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : DISPLAY_DATE.format(date);
    }
}
