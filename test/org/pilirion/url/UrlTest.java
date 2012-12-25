package org.pilirion.url;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 19.6.12
 * Time: 15:31
 */
public class UrlTest {
    @Test
    public void testGetBasePath() throws Exception {
        Url url = new Url("/user/path/");
        String expected = "/user/path";
        String result = url.getBasePath();
        assertEquals(expected, result);
    }

    @Test
    public void testGetBasePath2() throws Exception {
        Url url = new Url("/user/path");
        String expected = "/user/path";
        String result = url.getBasePath();
        assertEquals(expected, result);
    }

    @Test
    public void testGetBasePath3() throws Exception {
        Url url = new Url("/user/path/:id/:text");
        String expected = "/user/path";
        String result = url.getBasePath();
        assertEquals(expected, result);
    }
}
