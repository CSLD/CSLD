package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.RatingService;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
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
public class RatingsResultPanel extends Panel {
    private static final int NUM_RATINGS = 10;

    @SpringBean
    RatingService ratingService;

    private final IModel<Game> model;
    private final Component componentToRefresh;
    private final Component playedToRefresh;

    private Model<Integer> myRating;
    private RatingsArrayModel ratingsArrayModel;

    private PlayedPanel playedPanel;

    /**
     * Model to provide color for the game
     */
    private class RatingColorModel extends AbstractReadOnlyModel<String> {

        @Override
        public String getObject() {
            double ratingOfGame = model.getObject().getTotalRating() != null ? model.getObject().getTotalRating() : 0;
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
            double ratingOfGame = model.getObject().getTotalRating() != null ? model.getObject().getTotalRating()/10 : 0;
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
            if(model.getObject().getAmountOfRatings() > 3) {
                for(Rating rating: model.getObject().getRatings()) {
                    array[rating.getRating() - 1]++;
                }
                int maxRatings = 0;
                for(int actRating: array){
                    if(actRating > maxRatings) {
                        maxRatings = actRating;
                    }
                }
                for(int i = 0; i < array.length; i++){
                    array[i] = (int)(((double) array[i] / (double)maxRatings) * 100);
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
        super(id);
        this.model = model;
        this.componentToRefresh = componentToRefresh;
        this.playedToRefresh = playedToRefresh;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        Game game = model.getObject();

        playedPanel = new PlayedPanel("playedPanel", model, new Component[] { componentToRefresh, playedToRefresh });
        add(playedPanel);

        Label finalRating = new Label("ratingResult", new RatingResultModel());
        finalRating.add(new AttributeAppender("class",new RatingColorModel(), " "));
        finalRating.setOutputMarkupId(true);
        add(finalRating);

        // This is example of model refreshed on configure
        myRating = Model.of(0);
        Label myResult = new Label("myResult", myRating);
        add(myResult);

        Label amountOfResults = new Label("amountOfResults", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return model.getObject().getRatings().size();
            }
        });
        add(amountOfResults);

        ratingsArrayModel = new RatingsArrayModel();

        // Add stars & bars
        List<Integer> nums = new ArrayList<Integer>();
        for(int i=NUM_RATINGS; i>=1; i--) nums.add(i);

        add(new ListView<Integer>("starsAndBars", nums) {
            @Override
            protected void populateItem(ListItem<Integer> item) {
                // Add label
                item.add(new Label("number", item.getModel()));

                // Add class to the bar
                WebMarkupContainer bar = new WebMarkupContainer("bar");
                bar.add(new AttributeAppender("class", Model.of("bar"+item.getModelObject()), " "));
                item.add(bar);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        // Refresh my rating
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        if(logged != null){
            try {
                Rating mine = ratingService.getUserRatingOfGame(logged.getId(), model.getObject().getId());
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
