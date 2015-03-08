package cz.larpovadatabaze.components.panel.user;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.SignOut;
import cz.larpovadatabaze.components.page.user.UpdateUserPage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;

/**
 * It shows info about actually logged user.
 */
public class LoggedBoxPanel extends AbstractCsldPanel<CsldUser> {
    private boolean initiated = false;

    @SpringBean
    CsldUserService csldUserService;

    @SpringBean
    ImageService imageService;

    private class UserModel extends LoadableDetachableModel<CsldUser> {
        @Override
        protected CsldUser load() {
            return ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        }
    }

    public LoggedBoxPanel(String id) {
        super(id);
        setDefaultModel(new UserModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add user info
        Integer loggedUserId = getModelObject().getId();

        final Label loggedUserName = new Label("loggedUserName", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return getModelObject().getPerson().getName();
            }
        });

        add(new UserIcon("loggedUserImage", getModel()));
        add(loggedUserName);

        PageParameters params = new PageParameters(); params.add("id", loggedUserId);
        final BookmarkablePageLink<CsldBasePage> loggedUserLink =
            new BookmarkablePageLink<CsldBasePage>("loggedUserLink", UserDetail.class, params);
        add(loggedUserLink);


        // Add user buttons
        add(new BookmarkablePageLink<CsldBasePage>("settings", UpdateUserPage.class, params));
        add(new BookmarkablePageLink<CsldBasePage>("logout", SignOut.class));
    }

}
