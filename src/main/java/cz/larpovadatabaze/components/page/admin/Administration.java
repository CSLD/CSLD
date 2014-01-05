package cz.larpovadatabaze.components.page.admin;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 *
 */
@AuthorizeInstantiation({"Editor","Admin"})
public class Administration extends CsldBasePage {
    public Administration(){
        super();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BookmarkablePageLink<CsldBasePage> manageUserRights =
                new BookmarkablePageLink<CsldBasePage>("manageUserRights", ManageUserRightsPage.class);
        manageUserRights.setVisible(UserUtils.getLoggedUser().getRole().equals(CsldRoles.ADMIN.getRole()));
        add(manageUserRights);

        BookmarkablePageLink<CsldBasePage> manageLabels =
                new BookmarkablePageLink<CsldBasePage>("manageLabels", ManageLabelsPage.class);
        add(manageLabels);
    }
}
