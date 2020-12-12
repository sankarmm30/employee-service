package com.takeaway.challenge.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static boolean isNull(final Object value) {

        return value == null;
    }

    public static boolean isNotNull(final Object value) {

        return value != null;
    }
}
