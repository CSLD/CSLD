package cz.larpovadatabaze.components.panel.user;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.CsldUser;

/**
 * This Panel gets List of Users and simply creates list of them with links to their details.
 */
public class SimpleListUsersPanel extends Panel {
    private final IModel<List<CsldUser>> model;


    public SimpleListUsersPanel(String id, IModel<List<CsldUser>> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ListView<CsldUser> users  = new ListView<CsldUser>("users", model) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser user = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", user.getId());

                final BookmarkablePageLink<CsldUser> authorName =
                        new BookmarkablePageLink<CsldUser>("user", UserDetailPage.class, params);

                authorName.add(new Label("userName", user.getPerson().getName()));
                authorName.add(new Label("userNickname", user.getPerson().getNickNameView()));

                item.add(authorName);
            }
        };

        add(users);
    }
}
