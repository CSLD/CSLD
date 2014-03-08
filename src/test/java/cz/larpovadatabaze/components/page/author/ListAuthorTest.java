package cz.larpovadatabaze.components.page.author;

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
@ContextConfiguration(
        locations = {
                "file:src/main/resources/spring-web.xml",
                "file:src/test/resources/spring-test.xml"
        }
)
public class ListAuthorTest {
    private WicketTester tester;

    @Before
    public void setUp(){
        tester = new WicketTester();
    }

    @Test
    public void testHomePageBasicRender() {
        tester.startPage(ListAuthor.class);
        tester.assertRenderedPage(ListAuthor.class);
    }
}
