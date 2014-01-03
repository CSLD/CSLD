package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.GroupIcon;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.GroupHasMember;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * It shows basic information about Group.
 */
public class GroupDetailPanel extends AbstractCsldPanel<CsldGroup> {
    public GroupDetailPanel(String id, IModel<CsldGroup> model) {
        super(id, model);
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

        addListOfAuthors(group);
    }

    private void addListOfAuthors(CsldGroup group){
        List<GroupHasMember> groupMembers = group.getMembers();
        List<CsldUser> members = new ArrayList<CsldUser>();
        for(GroupHasMember groupMember: groupMembers) {
            members.add(groupMember.getUser());
        }
        ListView<CsldUser> authorsOfGroup  = new ListView<CsldUser>("groupAuthors", members) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser author = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", author.getId());

                final BookmarkablePageLink<CsldUser> authorName =
                        new BookmarkablePageLink<CsldUser>("groupAuthor", UserDetail.class, params);

                authorName.add(new Label("groupAuthorName", author.getPerson().getName()));
                authorName.add(new Label("groupAuthorNickname", author.getPerson().getNickNameView()));

                item.add(authorName);
            }
        };
        add(authorsOfGroup);
    }
}
