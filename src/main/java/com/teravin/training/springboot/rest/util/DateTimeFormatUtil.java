package com.teravin.training.springboot.rest.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeFormatUtil {

    private DateTimeFormatUtil() {
    }

    public static String format(OffsetDateTime dateTime) {

        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")) : null;
    }
}