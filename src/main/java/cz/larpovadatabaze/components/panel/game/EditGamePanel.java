package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.CreateOrUpdateGamePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * This panel contains link to the page for editing game.
 * It is shown only to logged user, with rights toward the specific game.
 */
public class EditGamePanel extends Panel {
    private IModel<Game> model;

    public EditGamePanel(String id, IModel<Game> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", model.getObject().getId());
        BookmarkablePageLink<CsldBasePage> pageLink =
                new BookmarkablePageLink<CsldBasePage>("editGame", CreateOrUpdateGamePage.class, params);
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged == null) {
                isVisible = false;
            }
            if(logged != null && logged.getRole() <= CsldRoles.USER.getRole()){
                if(model.getObject() == null || model.getObject().getAuthors() == null || model.getObject().getAddedBy() == null) {
                    isVisible = false;
                } else if(!model.getObject().getAuthors().contains(logged) && !model.getObject().getAddedBy().equals(logged)){
                    isVisible = false;
                }
            }
        }

        setVisibilityAllowed(isVisible);
    }
}
