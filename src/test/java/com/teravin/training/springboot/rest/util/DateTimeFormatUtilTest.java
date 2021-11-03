package com.teravin.training.springboot.rest.util;

import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateTimeFormatUtilTest {

    @Test
    public void testFormat() {

        String result = DateTimeFormatUtil.format(OffsetDateTime.of(2019, 10, 14, 16, 15, 0, 0, ZoneOffset.of("+7")));
        assertEquals("14-Oct-2019 16:15:00", result);
    }

    @Test
    public void testFormatWithNullValue() {

        assertNull(DateTimeFormatUtil.format(null));
    }
}