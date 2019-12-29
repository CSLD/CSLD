package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.users.components.panel.CsldSignInPanel;

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
