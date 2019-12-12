package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This panel shows result of rating of a game. It has background based on the rating and the amount of rating as
 * number.
 */
public class RatingsResultPanel extends AbstractCsldPanel<Game> {
    private static final int NUM_RATINGS = 10;

    @SpringBean
    RatingService ratingService;

    private final Component componentToRefresh;
    private final Component playedToRefresh;

    private Model<Integer> myRating;
    private RatingsArrayModel ratingsArrayModel;

    /**
     * Model to provide color for the game
     */
    private class RatingColorModel extends AbstractReadOnlyModel<String> {

        @Override
        public String getObject() {
            double ratingOfGame = getModelObject().getTotalRating() != null ? getModelObject().getTotalRating() : 0;
            return Rating.getColorOf(ratingOfGame);
        }
    }

    /**
     * Model to provide textual rating for the game
     */
    private class RatingResultModel extends AbstractReadOnlyModel<String> {
        private DecimalFormat df = new DecimalFormat("0.0");

        @Override
        public String getObject() {
            double ratingOfGame = getModelObject().getAverageRating() != null ? getModelObject().getAverageRating()/10d : 0;
            return df.format(ratingOfGame);
        }
    }

    /**
     * Holds array of ratings. Value is cached and refreshed on-request.
     */
    private class RatingsArrayModel extends AbstractReadOnlyModel<int[]> {

        // Backing array
        private int[] array;

        /**
         * Refresh array values
         */
        public void recompute() {
            array = new int[10];
            Arrays.fill(array,0);
            if(getModelObject().getAmountOfRatings() > 3) {
                for(Rating rating: getModelObject().getRatings()) {
                    array[rating.getRating() - 1]++;
                }
                int maxRatings = 0;
                for(int actRating: array){
                    if(actRating > maxRatings) {
                        maxRatings = actRating;
                    }
                }
                for(int i = 0; i < array.length; i++){
                    if(maxRatings == 0) {
                        array[i] = 0;
                    } else {
                        array[i] = (int) (((double) array[i] / (double) maxRatings) * 100);
                    }
                }
            }
        }

        @Override
        public int[] getObject() {
            if (array == null) recompute();
            return array;
        }
    }

    /****************************************************************************/

    public RatingsResultPanel(String id, IModel<Game> model, Component componentToRefresh, Component playedToRefresh) {
        super(id, model);
        this.componentToRefresh = componentToRefresh;
        this.playedToRefresh = playedToRefresh;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        // Rating result
        add(new GameRatingBoxPanel("ratingBox", getModel()));

        // My result
        myRating = Model.of(0);
        Label myResult = new Label("myResult", myRating);
        add(myResult);

        // Amount of results
        Label amountOfResults = new Label("amountOfResults", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return getModelObject().getAmountOfRatings();
            }
        });
        add(amountOfResults);

        // Played panel
        PlayedPanel playedPanel = new PlayedPanel("playedPanel", getModel(), new Component[]{componentToRefresh, playedToRefresh});
        add(playedPanel);

        ratingsArrayModel = new RatingsArrayModel();

        // Add stars & bars
        List<Integer> nums = new ArrayList<Integer>();
        for(int i=NUM_RATINGS; i>=1; i--) nums.add(i);

        add(new ListView<Integer>("starsAndBars", nums) {
            @Override
            protected void populateItem(ListItem<Integer> item) {
                // Add label
                final int n = item.getModelObject();
                item.add(new Label("number", n));

                // Add class && width to the bar
                WebMarkupContainer bar = new WebMarkupContainer("bar") {
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);

                        int percent = ratingsArrayModel.getObject()[n-1];
                        tag.put("class", tag.getAttribute("class")+" "+Rating.getColorOf(n*10d));
                        tag.put("aria-valuenow", percent);
                        tag.put("style", "width: "+percent+"%");
                    }
                };
                item.add(bar);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        // Refresh my rating
        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(logged != null){
            try {
                Rating mine = ratingService.getUserRatingOfGame(logged.getId(), getModelObject().getId());
                if(mine != null){
                    myRating.setObject(mine.getRating());
                } else {
                    myRating.setObject(0);
                }
            } catch (WrongParameterException e) {
                e.printStackTrace();
                myRating.setObject(0);
            }
        } else {
            myRating.setObject(0);
        }

        // Recompute ratings
        ratingsArrayModel.recompute();

    }

    /* Renders necessary animation Javascript */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        StringBuilder js = new StringBuilder();
        js.append("$(document).ready(function() {\n");
        js.append("$('.bar span').hide();\n");

        for(int i=1; i<=NUM_RATINGS; i++) {
            js.append("$('.bar.bar").append(i).append("').animate({width: ").append(ratingsArrayModel.getObject()[i-1]).append("}, 1000);\n");
        }

        js.append("window.setTimeout(function() {\n" +
                  "  $('.bar span').fadeIn('slow');\n" +
                  "  }, 1000);\n");
        js.append("});\n");

        response.render(OnDomReadyHeaderItem.forScript(js));
    }
}
