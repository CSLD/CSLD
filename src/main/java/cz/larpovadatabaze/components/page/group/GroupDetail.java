package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created with IntelliJ IDEA.
 * User: lichmer
 * Date: 28.7.13
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class GroupDetail extends CsldBasePage {
    @SpringBean
    private GroupService groupService;

    public GroupDetail(PageParameters params){
        Integer groupId = params.get("id").to(Integer.class);
        CsldGroup group = groupService.getById(groupId);

        GroupDetailPanel groupPanel = new GroupDetailPanel("groupDetail", group);
        add(groupPanel);

        add(new GameListPanel("authoredGamesPanel",group.getAuthorsOf()));
    }
}
