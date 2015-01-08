package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.BookmarkableLinkWithLabel;
import cz.larpovadatabaze.components.page.game.TranslateGame;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class
        TranslateGamePanel extends Panel {
    private IModel<Game> model;

    public TranslateGamePanel(String id, IModel<Game> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", model.getObject().getId());
        BookmarkableLinkWithLabel pageLink =
                new BookmarkableLinkWithLabel("translateGame",
                        TranslateGame.class,
                        new StringResourceModel("game.translate", this, null, null),
                        Model.of(params));
        add(pageLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
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
