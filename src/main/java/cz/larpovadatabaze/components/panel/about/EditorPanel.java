package cz.larpovadatabaze.components.panel.about;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
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
    CsldUserService csldUserService;

    @SpringBean
    ImageService imageService;

    public EditorPanel(String id) {
        super(id);

        List<CsldUser> editors = csldUserService.getEditors();
        ListView<CsldUser> editorsList = new ListView<CsldUser>("listModerator", editors) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser moderator = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", moderator.getId());
                final BookmarkablePageLink<CsldBasePage> moderatorLink =
                        new BookmarkablePageLink<CsldBasePage>("moderatorLink", UserDetail.class, params);
                final UserIcon moderatorImage = new UserIcon("moderatorImage", item.getModel());
                moderatorLink.add(moderatorImage);
                item.add(moderatorLink);

                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("moderatorLinkContent", UserDetail.class, params);
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
