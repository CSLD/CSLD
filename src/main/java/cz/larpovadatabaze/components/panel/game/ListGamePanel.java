package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.LabelService;
import cz.larpovadatabaze.services.RatingService;

/**
 * It contains all games in a pageable list, there are four possible ways to order
 * the list. Order alphabetically, Order by rating or order by amount of ratings, or
 * by amount of comments.
 */
public class ListGamePanel extends AbstractCsldPanel<FilterGame> {
    @SpringBean
    GameService gameService;
    @SpringBean
    LabelService labelService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    RatingService ratingService;
    @SpringBean
    CommentService commentService;

    private SortableGameProvider sgp;

    private RatingsModel ratingsModel;
    private CommentsModel commentsModel;

    public ListGamePanel(String id, IModel<FilterGame> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser loggedUser =  CsldAuthenticatedWebSession.get().getLoggedUser();
        if(loggedUser != null) {
            ratingsModel = new RatingsModel(loggedUser.getId());
            commentsModel = new CommentsModel(loggedUser.getId());
        } else {
            ratingsModel = new RatingsModel(0);
            commentsModel = new CommentsModel(0);
        }

        sgp = new SortableGameProvider(getModel());
        final DataView<Game> propertyList = new DataView<Game>("listGames", sgp) {
            @Override
            protected void populateItem(Item<Game> item) {
                Game game = item.getModelObject();
                int itemIndex = game.getFirst() + item.getIndex() + 1;
                final Label orderLabel = new Label("order", itemIndex);
                item.add(orderLabel);

                item.add(new GameNameAndLabelsPanel("nameAndLabels", item.getModel()));

                final Label gameYear = new Label("gameYear", Model.of(game.getYear()));
                item.add(gameYear);

                Long totalRating = Math.round(game.getTotalRating());

                WebMarkupContainer ratingWrapper = new WebMarkupContainer("ratingWrapper");
                item.add(ratingWrapper);
                ratingWrapper.add(new AttributeAppender("class", Model.of(Rating.getColorOf(totalRating)), " "));

                DecimalFormat df = new DecimalFormat("0.0");
                final Label gameRating = new Label("rating", Model.of(df.format((double) totalRating / 10d)));
                ratingWrapper.add(gameRating);

                Long averageRating = (totalRating == 0)?0:Math.round(game.getAverageRating());
                final Label average = new Label("average", Model.of(df.format((double) averageRating / 10d)));
                ratingWrapper.add(average);

                final Label gameRatings = new Label("ratings", game.getAmountOfRatings());
                if(ratingsModel.getObject().contains(game)){
                    gameRatings.add(new AttributeAppender("class"," rated"));
                }
                item.add(gameRatings);

                final Label gameComments = new Label("comments", game.getAmountOfComments());
                if(commentsModel.getObject().contains(game)){
                    gameComments.add(new AttributeAppender("class"," commented"));
                }
                item.add(gameComments);
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }

    private class CommentsModel extends LoadableDetachableModel<List<Game>> {
        final int userId;

        private CommentsModel(int userId) {
            this.userId = userId;
        }

        @Override
        protected List<Game> load() {
            if(userId == 0) {
                return new ArrayList<Game>();
            } else {
                return new ArrayList<Game>(commentService.getGamesCommentedByUser(userId));
            }
        }
    }

    private class RatingsModel extends LoadableDetachableModel<List<Game>> {
        final int userId;

        private RatingsModel(int userId) {
            this.userId = userId;
        }

        @Override
        protected List<Game> load() {
            if(userId == 0) {
                return new ArrayList<Game>();
            } else {
                return new ArrayList<Game>(gameService.getGamesRatedByUser(userId));
            }
        }
    }
}
