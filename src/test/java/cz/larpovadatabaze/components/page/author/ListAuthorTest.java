package cz.larpovadatabaze.components.page.author;

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
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 7.9.13
 * Time: 10:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-web.xml","classpath:hibernate.cfg.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class ListAuthorTest {
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
        tester.startPage(ListAuthor.class);
        tester.assertRenderedPage(ListAuthor.class);
    }
}
