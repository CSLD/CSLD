package cz.larpovadatabaze.donations.components;

import cz.larpovadatabaze.components.page.CsldBasePage;

/**
 * Page with information about donations and donators.
 */
public class DonationPage extends CsldBasePage {
    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Donors("donors"));
    }
}
