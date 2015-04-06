package cz.larpovadatabaze.components.page.group;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.group.AddAuthorsToGroupPanel;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.components.panel.group.EditGroupPanel;
import cz.larpovadatabaze.components.panel.group.GroupDetailPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;

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
            throw new RestartResponseException(ListGroupPage.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldGroup group = (CsldGroup)getDefaultModelObject();

        GroupDetailPanel groupPanel = new GroupDetailPanel("groupDetail", (IModel<CsldGroup>)getDefaultModel());
        add(groupPanel);

        add(new AddGroupPanel("addGroup"));
        add(new EditGroupPanel("editGroup", (IModel<CsldGroup>)getDefaultModel()));
        add(new AddAuthorsToGroupPanel("addAuthorsToGroup", (IModel<CsldGroup>)getDefaultModel()));
        add(new GameListPanel("authoredGamesPanel", new AbstractReadOnlyModel<List<? extends Game>>() {
            @Override
            public List<? extends Game> getObject() {
                return ((CsldGroup)getDefaultModelObject()).getAuthorsOf();
            }
        }));

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(gameService);
        provider.setGroup(group);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));
    }
}
