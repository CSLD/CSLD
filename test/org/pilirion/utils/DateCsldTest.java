package org.pilirion.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 24.6.12
 * Time: 9:10
 */
public class DateCsldTest {
    @Test
    public void testParse() throws Exception {
        String dateString = "14.5.1965";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parse(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseReverse() throws Exception {
        String dateString = "1965.5.14";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parse(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseIntReverse() throws Exception {
        String dateString = "1965-5-14";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parse(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseInt() throws Exception {
        String dateString = "14-05-1965";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parse(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseCzech() throws Exception {
        String dateString = "14.5.1965";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parseCzech(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseCzechReverse() throws Exception {
        String dateString = "1965.05.14";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parseCzech(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseInternationalReverse() throws Exception {
        String dateString = "1965-5-14";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parseInternational(dateString);
        assertEquals(expected, result);
    }

    @Test
    public void testParseInternational() throws Exception {
        String dateString = "14-05-1965";
        Calendar cal = new GregorianCalendar(1965,4,14,0,0,0);
        Date expected = cal.getTime();
        Date result = DateCsld.parseInternational(dateString);
        assertEquals(expected, result);
    }
}
