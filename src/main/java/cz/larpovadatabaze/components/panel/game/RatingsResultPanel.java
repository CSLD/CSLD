package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This panel shows result of rating of a game. It has background based on the rating and the amount of rating as
 * number.
 */
public class RatingsResultPanel extends Panel {
    @SpringBean
    GameService gameService;
    private Game game;

    private Model<String> ratingColorModel;
    private Model<Integer> ratingOfGameModel;
    private Model<Game> ratersModel;

    public RatingsResultPanel(String id, Game game) {
        super(id);
        this.game = game;

        double ratingOfGame = game.getTotalRating();
        String ratingColor = Rating.getColorOf(ratingOfGame);

        WebMarkupContainer finalRating = new WebMarkupContainer("resultsOfRating");
        ratingColorModel = Model.of(ratingColor);
        finalRating.add(new AttributeAppender("class",ratingColorModel, " "));
        finalRating.setOutputMarkupId(true);

        ratingOfGameModel = Model.of((int) Math.round(ratingOfGame));
        Label rating = new Label("rating", ratingOfGameModel);
        rating.setOutputMarkupId(true);
        ratersModel = new Model<Game>(game);
        Label raters = new Label("raters", new StringResourceModel("game.ratedBy",this, ratersModel));
        raters.setOutputMarkupId(true);

        finalRating.add(rating);
        finalRating.add(raters);
        add(finalRating);
    }

    public void reload(AjaxRequestTarget target) {
        game = gameService.getById(game.getId());
        if(HbUtils.isProxy(game)){
            game = HbUtils.deproxy(game);
        }

        double ratingOfGame = game.getTotalRating();
        String ratingColor = Rating.getColorOf(ratingOfGame);
        ratingColorModel.setObject(ratingColor);
        ratingOfGameModel.setObject((int) Math.round(ratingOfGame));
        ratersModel.setObject(game);

        target.add(this);
    }
}
