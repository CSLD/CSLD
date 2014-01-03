package cz.larpovadatabaze.components.panel.user;

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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows info about actually logged user.
 */
public class LoggedBoxPanel extends AbstractCsldPanel<CsldUser> {
    private boolean initiated = false;

    @SpringBean
    CsldUserService csldUserService;

    @SpringBean
    ImageService imageService;

    private Integer loggedUserId;

    private WebMarkupContainer wrapper;

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
    protected void onConfigure() {
        super.onConfigure();

        if (!CsldAuthenticatedWebSession.get().isSignedIn()) {
            // Be invisible
            wrapper.setVisible(false);
        }
        else {
            // Be visible
            wrapper.setVisible(true);

            CsldUser user = getModelObject();
            if (!user.getId().equals(loggedUserId)) {
                // Replace components
                loggedUserId = user.getId();

                PageParameters params = new PageParameters();
                params.add("id", loggedUserId);
                final BookmarkablePageLink<CsldBasePage> loggedUserLink =
                        new BookmarkablePageLink<CsldBasePage>("loggedUserLink", UserDetail.class, params);
                final UserIcon loggedUserIcon = new UserIcon("loggedUserImage", getModel());
                loggedUserLink.add(loggedUserIcon);
                wrapper.add(loggedUserLink);

                final BookmarkablePageLink<CsldBasePage> loggedUserLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("loggedUserLinkContent", UserDetail.class, params);
                final Label loggedUserNick = new Label("loggedUserNick", new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return getModelObject().getPerson().getNickNameView();
                    }
                });
                final Label loggedUserName = new Label("loggedUserName", new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return getModelObject().getPerson().getName();
                    }
                });
                loggedUserLinkContent.add(loggedUserNick);
                loggedUserLinkContent.add(loggedUserName);
                wrapper.add(loggedUserLinkContent);

                final BookmarkablePageLink<CsldBasePage> editConfiguration =
                        new BookmarkablePageLink<CsldBasePage>("editConfiguration", UpdateUserPage.class, params);
                final BookmarkablePageLink<CsldBasePage> logout =
                        new BookmarkablePageLink<CsldBasePage>("logout", SignOut.class);
                wrapper.add(editConfiguration);
                wrapper.add(logout);
            }
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        wrapper = new WebMarkupContainer("wrapper");
        add(wrapper);
    }

}
