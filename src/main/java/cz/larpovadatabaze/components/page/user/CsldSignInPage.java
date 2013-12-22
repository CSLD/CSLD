package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.user.CsldSignInPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 8:45
 */
public final class CsldSignInPage extends CsldBasePage
{
    /**
     * Construct
     */
    public CsldSignInPage()
    {
        add(new CsldSignInPanel("signInPanel"));
    }
}
