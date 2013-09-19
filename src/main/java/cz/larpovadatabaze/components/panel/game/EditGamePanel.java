package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
public class EditGamePanel extends Panel {
    private Game toEdit;

    public EditGamePanel(String id, Game toEdit) {
        super(id);
        this.toEdit = toEdit;

        PageParameters params = new PageParameters();
        params.add("id", toEdit.getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("editGame", CreateOrUpdateGamePage.class, params);
        add(pageLink);
    }

    protected void onConfigure() {
        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged == null) {
                isVisible = false;
            }
            if(logged != null && logged.getRole() <= CsldRoles.USER.getRole()){
                if(!toEdit.getAuthors().contains(logged)){
                    isVisible = false;
                }
            }
        }

        setVisibilityAllowed(isVisible);
    }
}
