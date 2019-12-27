package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.models.FilterGame;
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
 *
 * User: Michal Kara Date: 29.3.15 Time: 14:26
 */
public class FilterGameTabsPanel extends AbstractCsldPanel<FilterGame> {

    /**
     * Config for tabs
     */
    private enum FilterTab {
        POPULAR_NEW(FilterGame.OrderBy.NUM_RATINGS_DESC, true, "game.filter.popularNew"),
        ALL_NEW(FilterGame.OrderBy.ADDED_DESC, true, "game.filter.allNew"),
        BEST_RATED(FilterGame.OrderBy.RATING_DESC, false, "game.filter.bestRated"),
        MOST_POPULAR(FilterGame.OrderBy.NUM_RATINGS_DESC, false, "game.filter.mostPopular"),
        MOST_COMMENTED(FilterGame.OrderBy.NUM_COMMENTS_DESC, false, "game.filter.mostCommented");

        private final FilterGame.OrderBy orderBy;
        private final boolean showOnlyNew;
        private final  String resourceKey;
        FilterTab(FilterGame.OrderBy orderBy, boolean showOnlyNew, String resourceKey) {
            this.orderBy = orderBy;
            this.showOnlyNew = showOnlyNew;
            this.resourceKey = resourceKey;
        }
    }

    public FilterGameTabsPanel(String id, IModel<FilterGame> model) {
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
                        FilterGame filter = FilterGameTabsPanel.this.getModelObject();
                        filter.setOrderBy(config.orderBy);
                        filter.setShowOnlyNew(config.showOnlyNew);

                        // Let page know filter changed
                        ((ListGamePage) getPage()).filterChanged(true, false, false);
                    }
                };
                item.add(link);

                // Add text to link
                link.add(new Label("text", new StringResourceModel(item.getModelObject().resourceKey, this, null)));

                // Add selected class when tab is selected
                link.add(new AttributeAppender("class", (IModel<String>) () -> {
                    FilterGame filter = FilterGameTabsPanel.this.getModelObject();
                    boolean selected = (filter.getOrderBy() == config.orderBy)
                            && (filter.isShowOnlyNew() == config.showOnlyNew);

                    return selected ? "active" : null;
                }, " "));
            }
        });
    }
}
