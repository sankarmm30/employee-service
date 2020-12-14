package com.takeaway.challenge.util;

import com.takeaway.challenge.constant.GlobalConstant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static boolean isNull(final Object value) {

        return value == null;
    }

    public static boolean isNotNull(final Object value) {

        return value != null;
    }

    public static String getFormattedDate(final LocalDate date) {

        return isNotNull(date)
                ? date.format(DateTimeFormatter.ofPattern(GlobalConstant.DATE_FORMAT))
                : null ;
    }

    public static String getFormattedTimestamp(final ZonedDateTime dateTime) {

        return isNotNull(dateTime)
                ? dateTime.format(DateTimeFormatter.ofPattern(GlobalConstant.DATE_TIME_FORMAT))
                : null ;
    }
}
