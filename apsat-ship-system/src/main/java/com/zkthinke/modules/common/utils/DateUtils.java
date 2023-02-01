package com.zkthinke.modules.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author weicb
 * @date 2020/3/12 01:46
 */
public class DateUtils {
    private DateUtils() {
    }

    public static int getYear(Long timestampMillis) {
        if (timestampMillis == null) {
            return 0;
        }
        return getLocalDateTime(timestampMillis).getYear();
    }

    public static int getMonthValue(Long timestampMillis) {
        if (timestampMillis == null) {
            return 0;
        }
        return getLocalDateTime(timestampMillis).getMonthValue();
    }

    public static int getDateValue(Long timestampMillis) {
        if (timestampMillis == null) {
            return 0;
        }
        return getLocalDateTime(timestampMillis).getDayOfMonth();
    }

    public static String formatDateTime(Long timestampMillis){
        if (timestampMillis == null) {
            timestampMillis = 0L;
        }
        LocalDateTime now = getLocalDateTime(timestampMillis);
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static LocalDateTime getLocalDateTime(Long timestampMillis) {
        return LocalDateTime.ofEpochSecond(timestampMillis / 1000, 0, ZoneOffset.ofHours(8));
    }

}
