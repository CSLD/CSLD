package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.game.ListLastGames;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * This panel shows information about last added games
 */
public class LastGamesPanel extends Panel {
    @SpringBean
    GameService gameService;
    private final static int MAX_CHARS = 160;
    private final static int INITIAL_AMOUNT_LAST_GAMES = 3;
    private final static int EXPANDED_AMOUNT_LAST_GAMES = 10;

    private IModel<List<Game>> gamesModel;

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

            PageParameters params = new PageParameters();
            params.add("id", game.getId());

            if(game.getImage() == null){
                game.setImage(cz.larpovadatabaze.entities.Image.getDefaultGame());
            }

            final BookmarkablePageLink<CsldBasePage> gameLink =
                    new BookmarkablePageLink<CsldBasePage>("gameIconLink", GameDetail.class, params);
            final Image gameLinkImage = new Image("gameIcon",
                    new PackageResourceReference(Csld.class, game.getImage().getPath()));
            gameLink.add(gameLinkImage);
            gameFragment.add(gameLink);

            String gameRatingColor = Rating.getColorOf(game.getTotalRating());
            Label gameRating = new Label("gameRating","");
            gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
            gameFragment.add(gameRating);

            final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                    new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, params);
            final Label gameName = new Label("gameName", game.getName());
            gameLinkContent.add(gameName);
            gameFragment.add(gameLinkContent);

            gameFragment.add(new Label("players", Model.of(game.getPlayers())));
            gameFragment.add(new Label("gameDescription",
                    Model.of(
                            game.getDescription().length() > MAX_CHARS ?
                                    game.getDescription().substring(0,MAX_CHARS) :
                                    game.getDescription()))
            );
            final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                    new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, params);
            gameFragment.add(gameMoreLink);
        }
    }

    public LastGamesPanel(String id) {
        super(id);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();;

        List<Game> toShow = gameService.getLastGames(EXPANDED_AMOUNT_LAST_GAMES);

        // XXX - this will throw exception if we ever have toofew games - XXX
        add(new GamesListView("visibleGamesView", toShow.subList(0, INITIAL_AMOUNT_LAST_GAMES)));
        add(new GamesListView("hiddenGamesView", toShow.subList(INITIAL_AMOUNT_LAST_GAMES, toShow.size())));

        final BookmarkablePageLink<CsldBasePage> allGames =
                new BookmarkablePageLink<CsldBasePage>("allGames", ListLastGames.class);
        add(allGames);
    }
}
