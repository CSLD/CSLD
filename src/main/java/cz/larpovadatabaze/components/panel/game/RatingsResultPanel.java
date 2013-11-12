package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This panel shows result of rating of a game. It has background based on the rating and the amount of rating as
 * number.
 */
public abstract class RatingsResultPanel extends Panel {
    @SpringBean
    GameService gameService;
    @SpringBean
    RatingService ratingService;

    private Game game;

    private Model<String> ratingColorModel;
    private Model<String> ratingOfGameModel;
    private Model<Integer> myRating;
    private Model<Integer> amountOfResults;

    public RatingsResultPanel(String id, Game game) {
        super(id);
        setOutputMarkupId(true);
        this.game = game;

        add(new PlayedPanel("playedPanel", game) {
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                RatingsResultPanel.this.onCsldAction(target, form);
            }
        });

        double ratingOfGame = game.getTotalRating() != null ? game.getTotalRating() : 0;
        String ratingColor = Rating.getColorOf(ratingOfGame);

        ratingColorModel = Model.of(ratingColor);
        DecimalFormat df = new DecimalFormat("0.0");
        ratingOfGameModel = Model.of(df.format(ratingOfGame / 10));
        Label finalRating = new Label("ratingResult", ratingOfGameModel);
        finalRating.add(new AttributeAppender("class",ratingColorModel, " "));
        finalRating.setOutputMarkupId(true);
        add(finalRating);

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        if(logged != null){
            try {
                Rating mine = ratingService.getUserRatingOfGame(logged.getId(), game.getId());
                if(mine != null){
                    myRating = Model.of(mine.getRating());
                } else {
                    myRating = Model.of(0);
                }
            } catch (WrongParameterException e) {
                e.printStackTrace();
                myRating = Model.of(0);
            }
        } else {
            myRating = Model.of(0);
        }
        Label myResult = new Label("myResult", myRating);
        add(myResult);

        this.amountOfResults = Model.of(game.getAmountOfRatings());
        Label amountOfResults = new Label("amountOfResults", this.amountOfResults);
        add(amountOfResults);

        int[] ratingsArray = new int[10];
        Arrays.fill(ratingsArray,0);
        for(Rating rating: game.getRatings()) {
            ratingsArray[rating.getRating() - 1]++;
        }
        int maxRatings = 0;
        for(int actRating: ratingsArray){
            if(actRating > maxRatings) {
                maxRatings = actRating;
            }
        }
        for(int i = 0; i < ratingsArray.length; i++){
            ratingsArray[i] = (int)(((double) ratingsArray[i] / (double)maxRatings) * 100);
        }

        add(new Label("widthOne",Model.of(ratingsArray[0])).setOutputMarkupId(true));
        add(new Label("widthTwo",Model.of(ratingsArray[1])).setOutputMarkupId(true));
        add(new Label("widthThree",Model.of(ratingsArray[2])).setOutputMarkupId(true));
        add(new Label("widthFour",Model.of(ratingsArray[3])).setOutputMarkupId(true));
        add(new Label("widthFive",Model.of(ratingsArray[4])).setOutputMarkupId(true));
        add(new Label("widthSix",Model.of(ratingsArray[5])).setOutputMarkupId(true));
        add(new Label("widthSeven",Model.of(ratingsArray[6])).setOutputMarkupId(true));
        add(new Label("widthEight",Model.of(ratingsArray[7])).setOutputMarkupId(true));
        add(new Label("widthNine",Model.of(ratingsArray[8])).setOutputMarkupId(true));
        add(new Label("widthTen",Model.of(ratingsArray[9])).setOutputMarkupId(true));
    }

    public void reload(AjaxRequestTarget target) {
        game = gameService.getById(game.getId());
        if(HbUtils.isProxy(game)){
            game = HbUtils.deproxy(game);
        }

        double ratingOfGame = game.getTotalRating() != null ? game.getTotalRating() : 0;
        String ratingColor = Rating.getColorOf(ratingOfGame);
        ratingColorModel.setObject(ratingColor);
        DecimalFormat df = new DecimalFormat("0.0");
        ratingOfGameModel.setObject(df.format(ratingOfGame / 10));

        int[] ratings = new int[10];
        Arrays.fill(ratings,0);
        for(Rating rating: game.getRatings()) {
            ratings[rating.getRating() - 1]++;
        }
        int maxRatings = 0;
        for(int actRating: ratings){
            if(actRating > maxRatings) {
                maxRatings = actRating;
            }
        }
        for(int i = 0; i < ratings.length; i++){
            ratings[i] = (int)(((double)ratings[i] / (double)maxRatings) * 100);
        }

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        try {
            Rating mine = ratingService.getUserRatingOfGame(logged.getId(), game.getId());
            myRating.setObject(mine.getRating());
        } catch (WrongParameterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        get("widthOne").setDefaultModel(Model.of(ratings[0]));
        get("widthTwo").setDefaultModel(Model.of(ratings[1]));
        get("widthThree").setDefaultModel(Model.of(ratings[2]));
        get("widthFour").setDefaultModel(Model.of(ratings[3]));
        get("widthFive").setDefaultModel(Model.of(ratings[4]));
        get("widthSix").setDefaultModel(Model.of(ratings[5]));
        get("widthSeven").setDefaultModel(Model.of(ratings[6]));
        get("widthEight").setDefaultModel(Model.of(ratings[7]));
        get("widthNine").setDefaultModel(Model.of(ratings[8]));
        get("widthTen").setDefaultModel(Model.of(ratings[9]));
        target.add(this);
        target.appendJavaScript("var widthTen = $(\"#widthTen\").html();\n" +
                "              var widthNine = $(\"#widthNine\").html();\n" +
                "              var widthEight = $(\"#widthEight\").html();\n" +
                "              var widthSeven = $(\"#widthSeven\").html();\n" +
                "              var widthSix = $(\"#widthSix\").html();\n" +
                "              var widthFive = $(\"#widthFive\").html();\n" +
                "              var widthFour = $(\"#widthFour\").html();\n" +
                "              var widthThree = $(\"#widthThree\").html();\n" +
                "              var widthTwo = $(\"#widthTwo\").html();\n" +
                "              var widthOne = $(\"#widthOne\").html();\n" +
                "\n" +
                "            $('.bar span').hide();\n" +
                "            $('#bar-ten').animate({\n" +
                "               width: widthTen}, 1000);\n" +
                "            $('#bar-nine').animate({\n" +
                "               width: widthNine}, 1000);\n" +
                "            $('#bar-eight').animate({\n" +
                "               width: widthEight}, 1000);\n" +
                "            $('#bar-seven').animate({\n" +
                "               width: widthSeven}, 1000);\n" +
                "            $('#bar-six').animate({\n" +
                "               width: widthSix}, 1000);\n" +
                "            $('#bar-five').animate({\n" +
                "               width: widthFive}, 1000);\n" +
                "            $('#bar-four').animate({\n" +
                "               width: widthFour}, 1000);\n" +
                "            $('#bar-three').animate({\n" +
                "               width: widthThree}, 1000);\n" +
                "            $('#bar-two').animate({\n" +
                "               width: widthTwo}, 1000);\n" +
                "            $('#bar-one').animate({\n" +
                "               width: widthOne}, 1000);");
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
