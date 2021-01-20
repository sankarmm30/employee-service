package com.sandemo.hrms.util;

import com.sandemo.hrms.constant.GlobalConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(JUnit4.class)
public class UtilTest {

    private static final String TEST_DATE = "2020-01-01";
    private static final String TEST_TIMESTAMP = "2020-12-13T02:28:48.645+0100";

    @Test
    public void testIsNull() {

        Assert.assertTrue(Util.isNull(null));
        Assert.assertFalse(Util.isNull(""));
    }

    @Test
    public void testIsNotNull() {

        Assert.assertFalse(Util.isNotNull((null)));
        Assert.assertTrue(Util.isNotNull(""));
    }

    @Test
    public void testGetFormattedDate() {

        Assert.assertEquals(TEST_DATE, Util.getFormattedDate(LocalDate.of(2020, 01, 01)));
        Assert.assertNull(Util.getFormattedDate(null));
    }

    @Test
    public void testGetFormattedTimestamp() {

        ZonedDateTime inputDateTime = ZonedDateTime.parse(TEST_TIMESTAMP, DateTimeFormatter.ofPattern(GlobalConstant.DATE_TIME_FORMAT));

        Assert.assertEquals(TEST_TIMESTAMP, Util.getFormattedTimestamp(inputDateTime));
        Assert.assertNull(Util.getFormattedTimestamp(null));
    }
}
