package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.GameIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It shows info about randomly choosen Larp from database.
 */
public class RandomLarpPanel extends AbstractCsldPanel<Game> {
    @SpringBean
    GameService gameService;

    @SpringBean
    ImageService imageService;

    private boolean show = true;

    private class GameModel extends LoadableDetachableModel<Game> {
        private final int gameId;

        public GameModel() {
            gameId = gameService.getRandomGame().getId();
        }


        @Override
        protected Game load() {
            return gameService.getById(gameId);
        }
    }

    public RandomLarpPanel(String id) {
        super(id, null);
    }


    @Override
    protected void onInitialize() {
        setDefaultModel(new GameModel());

        super.onInitialize();

        Game game = getModelObject();
        if(game == null){
            game = Game.getEmptyGame();
            game.setId(-1);
            show = false;
        }

        PageParameters params = new PageParameters();
        params.add("id", game.getId());

        final BookmarkablePageLink<CsldBasePage> gameLink =
                new BookmarkablePageLink<CsldBasePage>("gameIconLink", GameDetail.class, params);
        final GameIcon gameLinkImage = new GameIcon("gameIcon", getModel());
        gameLink.add(gameLinkImage);
        add(gameLink);

        Double rating =game.getTotalRating();
        if(rating == null){
            rating = 0d;
        }
        String gameRatingColor = Rating.getColorOf(Math.round(rating));
        Label gameRating = new Label("gameRating","");
        gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
        add(gameRating);

        final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, params);
        final Label gameName = new Label("gameName", game.getName());
        gameLinkContent.add(gameName);
        add(gameLinkContent);

        add(new Label("players", Model.of(game.getPlayers())));
        add(new Label("year", Model.of(game.getYear())));
        add(new Label("rating", Model.of(Math.round((game.getTotalRating() != null) ? game.getTotalRating() : 0))));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(show);
    }
}
