package org.pilirion.url;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 19.6.12
 * Time: 12:08
 */
public class TranslatorTest {
    @org.junit.Test
    public void testTranslate() throws Exception {
        // Je potřeba do něj přidat sadu route a u jednotlivých url následně řešit korektní použití route.
        Translator translator = CsldTranslator.getTranslator();

        String routeToTranslate = "/user/detail";
        String expected = "/user/detail.jsp";
        String result = translator.translate(routeToTranslate);
        assertEquals(expected, result);

        routeToTranslate = "/user/edit/";
        expected = "/user/edit.jsp";
        result = translator.translate(routeToTranslate);
        assertEquals(expected, result);

        routeToTranslate = "/game/list/3";
        expected = "/game/list.jsp";
        result = translator.translate(routeToTranslate);
        assertEquals(expected, result);

        routeToTranslate = "/user/register";
        expected = "/user/register.jsp";
        result = translator.translate(routeToTranslate);
        assertEquals(expected, result);

        routeToTranslate = "/game/detail/4";
        expected = "/game/detail.jsp?id=4";
        result = translator.translate(routeToTranslate);
        assertEquals(expected, result);
    }
}
