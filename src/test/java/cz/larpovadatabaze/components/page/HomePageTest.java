package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.Csld;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-web.xml","classpath:hibernate.cfg.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class HomePageTest {
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
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
    }
}
