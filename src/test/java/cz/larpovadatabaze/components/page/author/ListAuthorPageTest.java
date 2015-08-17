package cz.larpovadatabaze.components.page.author;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.RootConfig;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
public class ListAuthorPageTest extends AcceptanceTest {
    @Test
    public void testHomePageBasicRender() {
        tester.startPage(ListAuthorPage.class);
        tester.assertRenderedPage(ListAuthorPage.class);
    }
}
