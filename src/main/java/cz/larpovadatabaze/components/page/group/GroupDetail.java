package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.group.AddAuthorsToGroupPanel;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.components.panel.group.EditGroupPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This Page shows detailed info about one of the groups.
 */
public class GroupDetail extends CsldBasePage {
    @SpringBean
    GroupService groupService;

    public GroupDetail(PageParameters params){
        Integer groupId = params.get("id").to(Integer.class);
        CsldGroup group = groupService.getById(groupId);

        GroupDetailPanel groupPanel = new GroupDetailPanel("groupDetail", group);
        add(groupPanel);

        add(new AddGroupPanel("addGroup"));
        add(new EditGroupPanel("editGroup", group));
        add(new AddAuthorsToGroupPanel("addAuthorsToGroup", group));
        add(new GameListPanel("authoredGamesPanel",group.getAuthorsOf()));
    }
}
