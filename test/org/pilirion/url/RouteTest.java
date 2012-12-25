package org.pilirion.url;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 19.6.12
 * Time: 13:16
 */
public class RouteTest {
    @Test
    public void testTranslate() throws Exception {
        Route route = new Route("/user/edit/:id/:text","/user/edit.jsp");
        String expected = "/user/edit.jsp?id=1&text=fgh";
        String result = route.translate("/user/edit/1/fgh");
        assertEquals(expected, result);
    }

    @Test
    public void testTranslate2() throws Exception {
        Route route = new Route("/user/edit/:id/:text","/user/edit.jsp");
        String expected = "/user/edit.jsp";
        String result = route.translate("/user/edit");
        assertEquals(expected, result);
    }

    @Test
    public void testTranslate3() throws Exception {
        Route route = new Route("/user/edit/","/user/edit.jsp");
        String expected = "/user/edit.jsp";
        String result = route.translate("/user/edit");
        assertEquals(expected, result);
    }

    @Test
    public void testTranslate4() throws Exception {
        Route route = new Route("/user/edit/:id/:text","/user/edit.jsp");
        String expected = "/user/edit.jsp?id=1";
        String result = route.translate("/user/edit/1");
        assertEquals(expected, result);
    }
}
