package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.components.page.CreateOrUpdateGamePage;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.CsldRoles;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;
import java.util.stream.Collectors;

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
            CsldUser logged = (CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged == null) {
                isVisible = false;
            }

            if(logged != null && logged.getRole() <= CsldRoles.USER.getRole()){
                List<Integer> ids = model.getObject().getAuthors().stream().map(CsldUser::getId).collect(Collectors.toList());
                if(model.getObject() == null || model.getObject().getAuthors() == null || model.getObject().getAddedBy() == null) {
                    isVisible = false;
                } else if(!ids.contains(logged.getId()) &&
                        !(model.getObject().getAddedBy().getId().equals(logged.getId()))){
                    isVisible = false;
                }
            }
        }

        setVisibilityAllowed(isVisible);
    }
}
