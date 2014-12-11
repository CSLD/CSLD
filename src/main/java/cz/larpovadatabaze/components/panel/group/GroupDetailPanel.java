package cz.larpovadatabaze.components.panel.group;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.GroupIcon;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.GroupHasMember;
import cz.larpovadatabaze.utils.UserUtils;

/**
 * It shows basic information about Group.
 */
public class GroupDetailPanel extends AbstractCsldPanel<CsldGroup> {

    private UserList administrators;

    public GroupDetailPanel(String id, IModel<CsldGroup> model) {
        super(id, model);
    }

    private static class UserList extends ListView<CsldUser> {
        public UserList(String id, IModel<? extends List<? extends CsldUser>> model) {
            super(id, model);
        }

        @Override
        protected void populateItem(ListItem<CsldUser> item) {
            CsldUser author = item.getModelObject();

            PageParameters params = new PageParameters();
            params.add("id", author.getId());

            final BookmarkablePageLink<CsldUser> authorName =
                new BookmarkablePageLink<CsldUser>("link", UserDetail.class, params);

            authorName.add(new Label("name", author.getPerson().getName()));
            authorName.add(new Label("nickname", author.getPerson().getNickNameView()));

            item.add(authorName);
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

        addListOfAuthors();

        // Add administrators
        administrators = new UserList("groupAdministrators", new AbstractReadOnlyModel<List<? extends CsldUser>>() {
            @Override
            public List<? extends CsldUser> getObject() {
                return getModelObject().getAdministrators();
            }
        });
        add(administrators);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        administrators.setVisible(UserUtils.isAdminOfGroup(((CsldGroup) getDefaultModelObject())));
    }

    private void addListOfAuthors(){
        ListView<CsldUser> authorsOfGroup  = new UserList("groupAuthors", new LoadableDetachableModel<List<? extends CsldUser>>() {
            @Override
            protected List<? extends CsldUser> load() {
                List<GroupHasMember> groupMembers = getModelObject().getMembers();
                List<CsldUser> members = new ArrayList<CsldUser>();
                for(GroupHasMember groupMember: groupMembers) {
                    members.add(groupMember.getUser());
                }

                return members;
            }
        });

        add(authorsOfGroup);
    }
}
