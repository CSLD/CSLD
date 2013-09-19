package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.SignOut;
import cz.larpovadatabaze.components.page.user.UpdateUserPage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
public class LoggedBoxPanel extends Panel {
    private boolean initiated = false;
    @SpringBean
    CsldUserService csldUserService;

    public LoggedBoxPanel(String id) {
        super(id);

        init();
    }

    private void init(){
        if(CsldAuthenticatedWebSession.get().isSignedIn()){
            if(!initiated){
                initiated = true;

                CsldAuthenticatedWebSession webSession = (CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get();
                CsldUser loggedUser = webSession.getLoggedUser();

                PageParameters params = new PageParameters();
                params.add("id", loggedUser.getId());
                final BookmarkablePageLink<CsldBasePage> moderatorLink =
                        new BookmarkablePageLink<CsldBasePage>("loggedUserLink", UserDetail.class, params);
                final Image moderatorImage = new Image("loggedUserImage",
                        new ContextRelativeResource(loggedUser.getImage().getPath()));
                moderatorLink.add(moderatorImage);
                add(moderatorLink);

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("loggedUserLinkContent", UserDetail.class, params);
                final Label moderatorNick = new Label("loggedUserNick", loggedUser.getPerson().getNickname());
                final Label moderatorName = new Label("loggedUserName", loggedUser.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                add(moderatorLinkContent);

                final BookmarkablePageLink<CsldBasePage> editConfiguration =
                        new BookmarkablePageLink<CsldBasePage>("editConfiguration", UpdateUserPage.class, params);
                final BookmarkablePageLink<CsldBasePage> logout =
                        new BookmarkablePageLink<CsldBasePage>("logout", SignOut.class);
                add(editConfiguration);
                add(logout);
            }
        }
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
        init();
    }
}
