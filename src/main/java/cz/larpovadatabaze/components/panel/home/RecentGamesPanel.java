package cz.larpovadatabaze.components.panel.home;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.OwlCarouselResourceReference;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;

/**
 * This panel shows information about last added games
 */
public class RecentGamesPanel extends Panel {
    @SpringBean
    GameService gameService;

    private final static int AMOUNT_OF_GAMES = 6;

    private final static DecimalFormat ratingFormat = new DecimalFormat("0.0");

    private WebMarkupContainer carousel;

    public RecentGamesPanel(String id) {
        super(id);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        carousel = new WebMarkupContainer("carousel");
        carousel.setOutputMarkupId(true);
        add(carousel);

        // Add recent games
        List<Game> recentGames = gameService.getLastGames(AMOUNT_OF_GAMES);
        for(int i=0; i<6; i++) {
            carousel.add(createGameBox("recent" + (i + 1), recentGames.get(i)));
        }

        // Add most popular games
        List<Game> mostPopularGames = gameService.getMostPopularGames(AMOUNT_OF_GAMES);
        for(int i=0; i<6; i++) {
            carousel.add(createGameBox("popular" + (i + 1), mostPopularGames.get(i)));
        }
    }

    /**
     * Create box for the game
     * @param id Component id
     * @param game Game to work on
     * @return Component with the game
     */
    private Component createGameBox(String id, Game game) {
        Fragment f = new Fragment(id, "gameBox", this);

        // Rating
        String gameRatingColor = Rating.getColorOf(game.getTotalRating());
        Label gameRating = new Label("gameRating", ratingFormat.format(game.getTotalRating() / 10));
        gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
        f.add(gameRating);

        // Link && name
        final BookmarkablePageLink<CsldBasePage> gameLinkContent =
            new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, GameDetail.paramsForGame(game));
        final Label gameName = new Label("gameName", game.getName());
        gameLinkContent.add(gameName);
        f.add(gameLinkContent);

        // Number of players
        f.add(new Label("players", game.getPlayers()));

        // Comments
        f.add(new Label("comments", game.getAmountOfComments()));

        // Ratings
        f.add(new Label("ratings", game.getAmountOfRatings()));

        return f;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // We need carousel
        response.render(JavaScriptHeaderItem.forReference(OwlCarouselResourceReference.get()));

        // Add carousel Javascript
        PackageTextTemplate tt = new PackageTextTemplate(getClass(), "RecentGamesPanel.js");
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("carouselId", carousel.getMarkupId());
        response.render(OnDomReadyHeaderItem.forScript(tt.asString(args)));
    }
}
