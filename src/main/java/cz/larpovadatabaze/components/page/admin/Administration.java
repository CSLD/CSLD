package cz.larpovadatabaze.components.page.admin;

import cz.larpovadatabaze.components.page.CsldBasePage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 *
 */
@AuthorizeInstantiation({"Editor","Admin"})
public class Administration extends CsldBasePage {
    public Administration(){
        super();

        BookmarkablePageLink<CsldBasePage> manageUserRights =
                new BookmarkablePageLink<CsldBasePage>("manageUserRights", ManageUserRightsPage.class);
        add(manageUserRights);

        BookmarkablePageLink<CsldBasePage> manageLabels =
                new BookmarkablePageLink<CsldBasePage>("manageLabels", ManageLabelsPage.class);
        add(manageLabels);

        BookmarkablePageLink<CsldBasePage> manageComments =
                new BookmarkablePageLink<CsldBasePage>("manageComments", Administration.class);
        add(manageComments);
    }
}
