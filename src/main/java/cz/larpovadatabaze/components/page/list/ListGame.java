package cz.larpovadatabaze.components.page.list;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.detail.GameDetail;
import cz.larpovadatabaze.components.panel.GamesPanel;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.4.13
 * Time: 15:59
 */
public class ListGame extends CsldBasePage {
    @SpringBean
    GameService gameService;

    public ListGame() {
        SortableGameProvider sgp = new SortableGameProvider(gameService);
        final DataView<Game> propertyList = new DataView<Game>("listGames", sgp) {
            @Override
            protected void populateItem(Item<Game> item) {
                int itemIndex = item.getIndex() + 1;
                Game game = item.getModelObject();
                final Label orderLabel = new Label("order", itemIndex);
                item.add(orderLabel);

                PageParameters params = new PageParameters();
                params.add("id",game.getId());
                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, params);
                final Label nameLabel = new Label("gameName", game.getName());
                gameLink.add(nameLabel);
                item.add(gameLink);

                final Label gameYear = new Label("gameYear", game.getYear());
                item.add(gameYear);

                final Label gameRating = new Label("rating", (int)game.getAverageRating());
                item.add(gameRating);

                List<Rating> ratings = (game.getRatings() != null) ?
                        game.getRatings() : new ArrayList<Rating>();
                final Label gameRatings = new Label("ratings", ratings.size());
                item.add(gameRatings);

                List<Comment> comments = (game.getComments() != null) ?
                        game.getComments() : new ArrayList<Comment>();
                final Label gameComments = new Label("comments", comments.size());
                item.add(gameComments);
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(new OrderByBorder("orderByName", "name", sgp)
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
        add(new PagingNavigator("navigator", propertyList));

        add(new GamesPanel("gamePanel"));
    }
}
