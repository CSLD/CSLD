package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.author.AuthorDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * This Panel gets List of Users and simply creates list of them with links to their details.
 */
public class SimpleListUsersPanel extends Panel {
    private List<CsldUser> users;

    public SimpleListUsersPanel(String id, List<CsldUser> users) {
        super(id);

        this.users = users;
        ListView<CsldUser> authorsOfGroup  = new ListView<CsldUser>("users", users) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser user = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", user.getId());

                final BookmarkablePageLink<CsldUser> authorName =
                        new BookmarkablePageLink<CsldUser>("user", UserDetail.class, params);

                authorName.add(new Label("userName", user.getPerson().getName()));
                authorName.add(new Label("userNickname", user.getPerson().getNickname()));

                item.add(authorName);
            }
        };
        add(authorsOfGroup);
    }

    public void reload(AjaxRequestTarget target, List<CsldUser> users) {
        this.users.removeAll(this.users);
        this.users.addAll(users);

        target.add(this);
    }
}
