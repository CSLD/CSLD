package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.providers.SortableGroupProvider;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 17:15
 */
public class ListGroupPage extends CsldBasePage {
    @SpringBean
    GroupService groupService;

    private SortableGroupProvider sgp;
    private WebMarkupContainer profileBar;
    private WebMarkupContainer main;

    public ListGroupPage() {
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        sgp = new SortableGroupProvider(groupService);
        sgp.setSort("name", SortOrder.DESCENDING);

        // Sort links
        profileBar = new WebMarkupContainer("profileBar");
        profileBar.setOutputMarkupId(true);
        add(profileBar);
        profileBar.add(new SortAjaxLink("orderByName", "name"));

        main = new WebMarkupContainer("main");
        main.setOutputMarkupId(true);
        add(main);
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
        groupList.setItemsPerPage(24L);

        main.add(groupList);
        main.add(new PagingNavigator("navigator", groupList));
    }

    // Refresh contents
    private void refreshContents(AjaxRequestTarget target) {
        target.add(profileBar);
        target.add(main);
    }

    /**
     * Class appender to make tab active when sorting by this rule is on
     */
    private class SortActiveClassAppender extends AttributeAppender {
        public SortActiveClassAppender(final String name) {
            super("class", new AbstractReadOnlyModel<String >() {
                @Override
                public String getObject() {
                    return name.equals(sgp.getSort().getProperty())?"active":"";
                }
            });
        }
    }

    /**
     * Ajax link to change sorting
     */
    private class SortAjaxLink extends AjaxLink {
        private final String columnName;

        public SortAjaxLink(String id, String columnName) {
            super(id);
            this.columnName = columnName;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(new SortActiveClassAppender(columnName));
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            sgp.setSort(columnName, SortOrder.DESCENDING);
            refreshContents(target);
        }
    }
}
