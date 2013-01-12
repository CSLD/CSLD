package org.pilirion.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 3.1.13
 * Time: 18:52
 */
public class FileUtilsTest {
    @Test
    public void testGetFileType() throws Exception {
        String result = FileUtils.getFileType("soubor.jpg");
        assertEquals("jpg", result);
    }
}
