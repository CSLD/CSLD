package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * It contains all games in a pageable list, there are four possible ways to order
 * the list. Order alphabetically, Order by rating or order by amount of ratings, or
 * by amount of comments.
 */
public class ListGamePanel extends Panel {
    @SpringBean
    GameService gameService;
    private SortableGameProvider sgp;

    @SuppressWarnings("unchecked")
    public ListGamePanel(String id) {
        super(id);

        sgp = new SortableGameProvider(gameService);
        final DataView<Game> propertyList = new DataView<Game>("listGames", sgp) {
            @Override
            protected void populateItem(Item<Game> item) {
                Game game = item.getModelObject();
                int itemIndex = game.getFirst() + item.getIndex() + 1;
                final Label orderLabel = new Label("order", itemIndex);
                item.add(orderLabel);

                PageParameters params = new PageParameters();
                params.add("id",game.getId());
                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, params);
                final Label nameLabel = new Label("gameName", Model.of(game.getName()));
                gameLink.add(nameLabel);
                item.add(gameLink);

                final Label gameYear = new Label("gameYear", Model.of(game.getYear()));
                item.add(gameYear);

                final Label gameRating = new Label("rating", Model.of(Math.round(game.getTotalRating())));
                item.add(gameRating);

                final Label gameRatings = new Label("ratings", game.getAmountOfRatings());
                item.add(gameRatings);
                final Image ratingsIcon = new Image("ratingsIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getRatingsIconPath()));
                item.add(ratingsIcon);

                final Label gameComments = new Label("comments", game.getAmountOfComments());
                item.add(gameComments);
                final Image commentsIcon = new Image("commentsIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getCommentsIconPath()));
                item.add(commentsIcon);
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(new OrderByBorder("orderByName", "form.wholeName", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByRating", "rating", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByRatingAmount", "ratingAmount", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });


        add(new OrderByBorder("orderByCommentAmount", "commentAmount", sgp)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });


        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }

    public void reload(AjaxRequestTarget target, FilterGame filterGame, List<cz.larpovadatabaze.entities.Label> labels) {
        sgp.setFilters(filterGame, labels);

        target.add(ListGamePanel.this);
    }
}
