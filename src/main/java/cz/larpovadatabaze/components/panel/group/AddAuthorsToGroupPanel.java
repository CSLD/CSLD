package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.ManageGroupPage;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 * This panel contains link to the page allowing to manage members of the group.
 * It is shown only to logged person, which has rights toward the group.
 */
public class AddAuthorsToGroupPanel extends Panel {
    private CsldGroup group;

    public AddAuthorsToGroupPanel(String id, CsldGroup group) {
        super(id);
        this.group = group;

        PageParameters params = new PageParameters();
        params.add("id",group.getId());

        Image createGameIcon = new Image("addAuthorsIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getPlusIconPath()));
        BookmarkablePageLink<CsldBasePage> createGameIconLink =
                new BookmarkablePageLink<CsldBasePage>("addAuthorsIconLink", ManageGroupPage.class, params);
        createGameIconLink.add(createGameIcon);
        add(createGameIconLink);

        BookmarkablePageLink<CsldBasePage> createAuthorLink =
                new BookmarkablePageLink<CsldBasePage>("addAuthorsLink", ManageGroupPage.class, params);
        add(createAuthorLink);
    }

    protected void onConfigure() {
        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged == null){
                isVisible = false;
            }
            if(logged != null && logged.getRole() <= CsldRoles.USER.getRole()){
                if(!group.getAdministrators().contains(logged)){
                    isVisible = false;
                }
            }
        }

        setVisibilityAllowed(isVisible);
    }
}
