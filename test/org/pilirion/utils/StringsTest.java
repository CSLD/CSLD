package org.pilirion.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StringsTest {
    @Test
    public void testRemoveLastNull() throws Exception {
        String test = null;
        String expected = "";
        String result = Strings.removeLast(test);
        assertEquals(expected, result);
    }

    @Test
    public void testRemoveLastEmpty() throws Exception {
        String test = "";
        String expected = "";
        String result = Strings.removeLast(test);
        assertEquals(expected, result);
    }

    @Test
    public void testRemoveLast() throws Exception {
        String test = "testingString/";
        String expected = "testingString";
        String result = Strings.removeLast(test);
        assertEquals(expected, result);
    }
}
