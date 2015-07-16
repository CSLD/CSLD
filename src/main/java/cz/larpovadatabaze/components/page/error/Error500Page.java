package cz.larpovadatabaze.components.page.error;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

import cz.larpovadatabaze.components.page.CsldBasePage;

public class Error500Page extends CsldBasePage {
    @Override
    protected Component provideAdvertisementsPanel(String id) {
        return new Label(id).setVisible(false);
    }
}
