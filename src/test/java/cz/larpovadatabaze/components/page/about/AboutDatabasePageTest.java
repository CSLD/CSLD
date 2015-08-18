package cz.larpovadatabaze.components.page.about;

import cz.larpovadatabaze.AcceptanceTest;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import cz.larpovadatabaze.Csld;

/**
 *
 */
public class AboutDatabasePageTest extends AcceptanceTest {
    @Test
    public void testAboutDatabaseIsRendered() {
        tester.startPage(AboutDatabasePage.class);
        tester.assertRenderedPage(AboutDatabasePage.class);
    }
}
