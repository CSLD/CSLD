package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.group.AddAuthorsToGroupPanel;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.components.panel.group.EditGroupPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This Page shows detailed info about one of the groups.
 */
public class GroupDetail extends CsldBasePage {
    @SpringBean
    GroupService groupService;
    @SpringBean
    GameService gameService;

    private class GroupModel extends LoadableDetachableModel<CsldGroup> {
        private final int groupId;

        private GroupModel(int groupId) {
            this.groupId = groupId;
        }

        @Override
        protected CsldGroup load() {
            return groupService.getById(groupId);
        }
    }

    public GroupDetail(PageParameters params){
        setDefaultModel(new GroupModel(params.get("id").to(Integer.class)));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldGroup group = (CsldGroup)getDefaultModelObject();

        GroupDetailPanel groupPanel = new GroupDetailPanel("groupDetail", (IModel<CsldGroup>)getDefaultModel());
        add(groupPanel);

        add(new AddGroupPanel("addGroup"));
        add(new EditGroupPanel("editGroup", group));
        add(new AddAuthorsToGroupPanel("addAuthorsToGroup", group));
        add(new GameListPanel("authoredGamesPanel",Model.ofList(group.getAuthorsOf())));

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(gameService);
        provider.setGroup(group);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));
    }
}
