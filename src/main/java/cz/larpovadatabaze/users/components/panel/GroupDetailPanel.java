package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.components.icons.GroupIcon;
import cz.larpovadatabaze.users.components.page.UserDetailPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * It shows basic information about Group.
 */
public class GroupDetailPanel extends AbstractCsldPanel<CsldGroup> {
    public GroupDetailPanel(String id, IModel<CsldGroup> model) {
        super(id, model);
    }

    private static class UserList extends ListView<CsldUser> {
        public UserList(String id, IModel<? extends List<CsldUser>> model) {
            super(id, model);
        }

        @Override
        protected void populateItem(ListItem<CsldUser> item) {
            item.add(new Label("separator", (item.getIndex() == 0) ? "" : ", "));

            CsldUser author = item.getModelObject();

            PageParameters params = new PageParameters();
            params.add("id", author.getId());

            final BookmarkablePageLink<CsldUser> link =
                new BookmarkablePageLink<CsldUser>("link", UserDetailPage.class, params);

            link.add(new Label("name", author.getPerson().getName()));
            link.add(new Label("nickname", author.getPerson().getNickNameView()));

            item.add(link);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final GroupIcon groupIcon = new GroupIcon("groupImage", getModel());
        add(groupIcon);

        CsldGroup group = getModelObject();
        add(new Label("name", group.getName()));
        int authoredLarps = group.getAuthorsOf().size();
        add(new Label("organized", authoredLarps));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }
}