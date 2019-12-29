package cz.larpovadatabaze.administration.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This page shall contain links to most of the tasks that editors takes care of.
 */
@AuthorizeInstantiation({"Editor", "Admin"})
public class AdministrationPage extends CsldBasePage {
    @SpringBean
    private AppUsers appUsers;

    public AdministrationPage() {
        super();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BookmarkablePageLink<CsldBasePage> manageUserRights =
                new BookmarkablePageLink<CsldBasePage>("manageUserRights", ManageUserRightsPage.class);
        manageUserRights.setVisible(appUsers.getLoggedUser().getRole().equals(CsldRoles.ADMIN.getRole()));
        add(manageUserRights);

        BookmarkablePageLink<CsldBasePage> manageLabels =
                new BookmarkablePageLink<CsldBasePage>("manageLabels", ManageLabelsPage.class);
        add(manageLabels);

        BookmarkablePageLink<CsldBasePage> showAuthorsWhoRatedTheirOwnGames =
                new BookmarkablePageLink<CsldBasePage>("authorRatedOwnGame", ShowAuthorsWhoRatedTheirGamesPage.class);
        add(showAuthorsWhoRatedTheirOwnGames);

    }
}
