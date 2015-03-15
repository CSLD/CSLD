package cz.larpovadatabaze.components.panel.home;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import cz.larpovadatabaze.components.common.icons.GameIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.game.ListLastGames;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageService;

/**
 * This panel shows information about last added games
 */
public class LastGamesPanel extends Panel {
    @SpringBean
    GameService gameService;

    @SpringBean
    ImageService imageService;

    private final static int MAX_CHARS_IN_DESCRIPTION = 150;
    private final static int INITIAL_AMOUNT_LAST_GAMES = 3;
    private final static int EXPANDED_AMOUNT_LAST_GAMES = 10;

    /**
     * List view of games
     */
    private class GamesListView extends ListView<Game> {
        public GamesListView(String id, List<? extends Game> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<Game> item) {
            Game game = item.getModelObject();

            Fragment gameFragment = new Fragment("game", "gameFragment", LastGamesPanel.this);
            item.add(gameFragment);

            PageParameters gameParams = GameDetail.paramsForGame(game);

            final BookmarkablePageLink<CsldBasePage> gameLink =
                    new BookmarkablePageLink<CsldBasePage>("gameIconLink", GameDetail.class, gameParams);
            final GameIcon gameLinkImage = new GameIcon("gameIcon", item.getModel());
            gameLink.add(gameLinkImage);
            gameFragment.add(gameLink);

            String gameRatingColor = Rating.getColorOf(game.getTotalRating());
            Label gameRating = new Label("gameRating","");
            gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
            gameFragment.add(gameRating);

            final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                    new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
            final Label gameName = new Label("gameName", game.getName());
            gameLinkContent.add(gameName);
            gameFragment.add(gameLinkContent);

            gameFragment.add(new Label("players", Model.of(game.getPlayers())));

            String gameDescription = Jsoup.parse(game.getDescription()).text();
            if (gameDescription.length() > MAX_CHARS_IN_DESCRIPTION) gameDescription = gameDescription.substring(0, MAX_CHARS_IN_DESCRIPTION);
            gameFragment.add(new Label("gameDescription", gameDescription));

            final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                    new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, gameParams);
            gameFragment.add(gameMoreLink);
        }
    }

    public LastGamesPanel(String id) {
        super(id);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        List<Game> toShow = gameService.getLastGames(EXPANDED_AMOUNT_LAST_GAMES);

        if(toShow.size() >= INITIAL_AMOUNT_LAST_GAMES) {
            add(new GamesListView("visibleGamesView", toShow.subList(0, INITIAL_AMOUNT_LAST_GAMES)));
            add(new GamesListView("hiddenGamesView", toShow.subList(INITIAL_AMOUNT_LAST_GAMES, toShow.size())));
        } else {
            add(new GamesListView("visibleGamesView", toShow));
            add(new GamesListView("hiddenGamesView", new ArrayList<Game>()));
        }

        final BookmarkablePageLink<CsldBasePage> allGames =
                new BookmarkablePageLink<CsldBasePage>("allGames", ListLastGames.class);
        add(allGames);
    }
}
