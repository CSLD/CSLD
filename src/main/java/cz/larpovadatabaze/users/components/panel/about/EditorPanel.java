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
 * It shows information about all editors of the database
 */
public class EditorPanel extends Panel {
    @SpringBean
    CsldUsers csldUsers;

    @SpringBean
    Images images;

    public EditorPanel(String id) {
        super(id);

        List<CsldUser> editors = csldUsers.getEditors();
        ListView<CsldUser> editorsList = new ListView<CsldUser>("listModerator", editors) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser moderator = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", moderator.getId());
                final BookmarkablePageLink<CsldBasePage> moderatorLink =
                        new BookmarkablePageLink<CsldBasePage>("moderatorLink", UserDetailPage.class, params);
                final UserIcon moderatorImage = new UserIcon("moderatorImage", item.getModel());
                moderatorLink.add(moderatorImage);
                item.add(moderatorLink);

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("moderatorLinkContent", UserDetailPage.class, params);
                final Label moderatorNick = new Label("moderatorNick", moderator.getPerson().getNickNameView());
                final Label moderatorName = new Label("moderatorName", moderator.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                item.add(moderatorLinkContent);
            }
        };
        add(editorsList);
    }
}
