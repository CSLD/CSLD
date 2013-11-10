package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.group.AddGroupPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.providers.SortableGroupProvider;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 17:15
 */
public class ListGroup extends CsldBasePage {
    @SpringBean
    GroupService groupService;

    public ListGroup() {
        SortableGroupProvider sgp = new SortableGroupProvider(groupService);
        final DataView<CsldGroup> groupList = new DataView<CsldGroup>("listGroups", sgp) {

            @Override
            protected void populateItem(Item<CsldGroup> item) {
                CsldGroup group = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", group.getId());

                final BookmarkablePageLink<CsldUser> groupDetail =
                        new BookmarkablePageLink<CsldUser>("groupDetail", GroupDetail.class, params);

                final Label label = new Label("groupName", group.getName());
                final Label authorsOf = new Label("amountOfGames", group.getAuthorsOf().size());
                groupDetail.add(label);
                final Label authorsOfAverage = new Label("averageOfAllGames", groupService.getAverageOfGroup(group));
                item.add(authorsOfAverage);

                item.add(groupDetail);
                item.add(authorsOf);
            }
        };
        groupList.setOutputMarkupId(true);
        groupList.setItemsPerPage(25L);

        add(groupList);
        add(new PagingNavigator("navigator", groupList));
        add(new AddGroupPanel("addGroup"));
    }
}
