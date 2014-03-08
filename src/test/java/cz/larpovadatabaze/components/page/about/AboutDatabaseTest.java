package cz.larpovadatabaze.components.page.about;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.HomePage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
public class AboutDatabaseTest {
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
        tester.startPage(AboutDatabase.class);
        tester.assertRenderedPage(AboutDatabase.class);
    }
}
