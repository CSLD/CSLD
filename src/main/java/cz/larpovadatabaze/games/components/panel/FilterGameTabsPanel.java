package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.games.components.page.ListGamePage;
import cz.larpovadatabaze.games.models.FilterGameDTO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.Arrays;

/**
 * Panel for tabs in
 * <p>
 * User: Michal Kara Date: 29.3.15 Time: 14:26
 */
public class FilterGameTabsPanel extends AbstractCsldPanel<FilterGameDTO> {

    /**
     * Config for tabs
     */
    private enum FilterTab {
        POPULAR_NEW(FilterGameDTO.OrderBy.NUM_RATINGS_DESC, true, "game.filter.popularNew"),
        ALL_NEW(FilterGameDTO.OrderBy.ADDED_DESC, true, "game.filter.allNew"),
        BEST_RATED(FilterGameDTO.OrderBy.RATING_DESC, false, "game.filter.bestRated"),
        MOST_POPULAR(FilterGameDTO.OrderBy.NUM_RATINGS_DESC, false, "game.filter.mostPopular"),
        MOST_COMMENTED(FilterGameDTO.OrderBy.NUM_COMMENTS_DESC, false, "game.filter.mostCommented");

        private final FilterGameDTO.OrderBy orderBy;
        private final boolean showOnlyNew;
        private final String resourceKey;

        FilterTab(FilterGameDTO.OrderBy orderBy, boolean showOnlyNew, String resourceKey) {
            this.orderBy = orderBy;
            this.showOnlyNew = showOnlyNew;
            this.resourceKey = resourceKey;
        }
    }

    public FilterGameTabsPanel(String id, IModel<FilterGameDTO> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ListView<FilterTab>("tabs", Arrays.asList(FilterTab.values())) {
            @Override
            protected void populateItem(ListItem<FilterTab> item) {
                final FilterTab config = item.getModelObject();

                // Create link
                AjaxLink<FilterTab> link = new AjaxLink<FilterTab>("link") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        // Affect filter
                        FilterGameDTO filter = FilterGameTabsPanel.this.getModelObject();
                        FilterGameTabsPanel.this.getModel().setObject(
                                new FilterGameDTO(filter, config.orderBy, config.showOnlyNew)
                        );

                        // Let page know filter changed
                        ((ListGamePage) getPage()).filterChanged(true, false, false);
                    }
                };
                item.add(link);

                // Add text to link
                link.add(new Label("text", new StringResourceModel(item.getModelObject().resourceKey, this, null)));

                // Add selected class when tab is selected
                link.add(new AttributeAppender("class", (IModel<String>) () -> {
                    FilterGameDTO filter = FilterGameTabsPanel.this.getModelObject();
                    boolean selected = (filter.getOrderBy() == config.orderBy)
                            && (filter.isShowOnlyNew() == config.showOnlyNew);

                    return selected ? "active" : null;
                }, " "));
            }
        });
    }
}
