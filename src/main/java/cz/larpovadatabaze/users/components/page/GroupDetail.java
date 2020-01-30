package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.games.components.panel.ListGamesWithAnnotations;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.users.components.panel.AddGroupPanel;
import cz.larpovadatabaze.users.components.panel.EditGroupPanel;
import cz.larpovadatabaze.users.components.panel.GroupDetailPanel;
import cz.larpovadatabaze.users.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.users.services.CsldGroups;
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
    CsldGroups csldGroups;
    @SpringBean
    AuthoredGames games;
    private final static String GROUP_ID_PARAMETER_NAME = "id";

    private class GroupModel extends LoadableDetachableModel<CsldGroup> {
        private final int groupId;

        private GroupModel(int groupId) {
            this.groupId = groupId;
        }

        @Override
        protected CsldGroup load() {
            return csldGroups.getById(groupId);
        }
    }

    public GroupDetail(PageParameters params){
        try {
            setDefaultModel(new GroupModel(params.get(GROUP_ID_PARAMETER_NAME).to(Integer.class)));
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

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(games);
        provider.setGroup(group);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));

        add(new AddGroupPanel("addGroup"));
        add(new EditGroupPanel("editGroup", (IModel<CsldGroup>)getDefaultModel()));
    }

    public static PageParameters paramsForGroup(CsldGroup group) {
        PageParameters pp = new PageParameters();

        if (group != null) {
            pp.add(GROUP_ID_PARAMETER_NAME, group.getId());
        }

        return pp;
    }
}
