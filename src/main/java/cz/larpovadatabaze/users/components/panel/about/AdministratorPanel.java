package cz.larpovadatabaze.users.components.panel.about;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.components.icons.UserIcon;
import cz.larpovadatabaze.users.components.page.UserDetailPage;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * It shows information about all administrators of the database.
 */
public class AdministratorPanel extends Panel {
    @SpringBean
    CsldUsers csldUsers;

    @SpringBean
    Images images;

    public AdministratorPanel(String id) {
        super(id);

        List<CsldUser> admins = csldUsers.getAdmins();
        ListView<CsldUser> editorsList = new ListView<CsldUser>("listAdministrator", admins) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser admin = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", admin.getId());
                final BookmarkablePageLink<CsldBasePage> adminLink =
                        new BookmarkablePageLink<CsldBasePage>("administratorLink", UserDetailPage.class, params);
                final UserIcon adminImage = new UserIcon("administratorImage", item.getModel());
                adminLink.add(adminImage);
                item.add(adminLink);

                final BookmarkablePageLink<CsldBasePage> administratorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("administratorLinkContent", UserDetailPage.class, params);
                final Label administratorNick = new Label("nicknameAdministrator", admin.getPerson().getNickNameView());
                final Label administratorName = new Label("nameAdministrator", admin.getPerson().getName());
                administratorLinkContent.add(administratorNick);
                administratorLinkContent.add(administratorName);
                item.add(administratorLinkContent);

                final Label administratorMail = new Label("administratorMail", admin.getPerson().getEmail());
                item.add(administratorMail);
            }
        };
        add(editorsList);

    }
}
