package cz.larpovadatabaze.components.page.about;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.components.panel.about.AboutDbPanel;
import org.junit.Test;

/**
 *
 */
public class AboutDatabasePageTest extends WithWicket {
    @Test
    public void testAboutDatabaseIsRendered() {
        tester.startPage(AboutDatabasePage.class);
        tester.assertRenderedPage(AboutDatabasePage.class);

        tester.assertComponent("rightPartAboutDb", AboutDbPanel.class);
    }
}
