package cz.larpovadatabaze.common.components.page;

import cz.larpovadatabaze.common.services.builders.CzechMasqueradeBuilder;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Simple class for creation of test database with necessary data. This data can be also used in tests.
 */
public class TestDatabase extends WebPage {
    @SpringBean
    private CzechMasqueradeBuilder masqueradeBuilder;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        masqueradeBuilder.build();
    }
}
