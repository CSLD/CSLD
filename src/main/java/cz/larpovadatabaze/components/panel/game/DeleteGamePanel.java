package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.DeleteGamePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * This panel contains link for deleting the game.
 */
public class DeleteGamePanel extends Panel {
    public DeleteGamePanel(String id, Game game) {
        super(id);

        PageParameters params = new PageParameters();
        params.add("id", game.getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("deleteGame", DeleteGamePage.class, params);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        boolean visible = false;
        if(logged != null){
            if(logged.getRole() > CsldRoles.USER.getRole()) {
                visible = true;
            }
        }
        setVisibilityAllowed(visible);
    }
}
