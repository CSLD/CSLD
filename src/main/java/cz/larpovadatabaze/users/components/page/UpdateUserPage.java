package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.panel.CreateOrUpdateUserPanel;
import cz.larpovadatabaze.users.components.panel.PersonDetailPanel;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
public class UpdateUserPage extends CsldBasePage {
    @SpringBean
    CsldUsers csldUsers;

    private class UserModel extends LoadableDetachableModel<CsldUser> {
        @Override
        protected CsldUser load() {
            // We reload user, since stored user may have not loaded some data
            CsldUser res = csldUsers.getById((CsldAuthenticatedWebSession.get()).getLoggedUser().getId());
            if (HbUtils.isProxy(res)) {
                res = HbUtils.deproxy(res);
            }

            return res;
        }
    }

    public UpdateUserPage(PageParameters params) {
        setDefaultModel(new UserModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new PersonDetailPanel("personDetail", (IModel<CsldUser>)getDefaultModel()));

        CsldUser user = (CsldUser)getDefaultModelObject();

        PageParameters pp = new PageParameters();
        pp.add(UserDetailPage.USER_ID_PARAMETER_NAME, user.getId());
        add(new BookmarkablePageLink<UpdateUserPage>("userProfileLink", UserDetailPage.class, pp));

        add(new CreateOrUpdateUserPanel("updateUser", user) {
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);

                CsldUser user = (CsldUser) object;
                PageParameters params = new PageParameters();
                params.add("id", user.getId());

                throw new RestartResponseException(UserDetailPage.class, params);
            }
        });
    }
}
