package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.components.panel.group.EditGroupPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
        try {
            setDefaultModel(new GroupModel(params.get("id").to(Integer.class)));
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(HomePage.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldGroup group = (CsldGroup)getDefaultModelObject();

        GroupDetailPanel groupPanel = new GroupDetailPanel("groupDetail", (IModel<CsldGroup>)getDefaultModel());
        add(groupPanel);

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(gameService);
        provider.setGroup(group);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));

        add(new AddGroupPanel("addGroup"));
        add(new EditGroupPanel("editGroup", (IModel<CsldGroup>)getDefaultModel()));
    }
}
