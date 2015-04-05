package cz.larpovadatabaze.components.page.author;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class ListAuthorPageTest {
    private WicketTester tester;

    @Before
    public void setUp(){
        tester = new WicketTester();
    }

    @Test
    public void testHomePageBasicRender() {
        tester.startPage(ListAuthorPage.class);
        tester.assertRenderedPage(ListAuthorPage.class);
    }
}
