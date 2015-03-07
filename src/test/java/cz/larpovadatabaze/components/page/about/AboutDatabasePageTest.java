package cz.larpovadatabaze.components.page.about;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import cz.larpovadatabaze.Csld;

/**
 *
 */
public class AboutDatabasePageTest {
    private WicketTester tester;

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private Csld csld;

    @Before
    public void setUp(){
        tester = new WicketTester(csld);
    }

    @Test
    public void testHomePageBasicRender() {
        tester.startPage(AboutDatabasePage.class);
        tester.assertRenderedPage(AboutDatabasePage.class);
    }
}
