package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.DeleteGamePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * This panel contains link for deleting the game.
 */
public class DeleteGamePanel extends Panel {

    private final int gameId;

    public DeleteGamePanel(String id, int gameId) {
        super(id);
        this.gameId = gameId;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", gameId);
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
