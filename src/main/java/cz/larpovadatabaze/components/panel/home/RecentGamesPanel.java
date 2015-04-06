package cz.larpovadatabaze.components.panel.home;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.larpovadatabaze.components.page.OwlCarouselResourceReference;
import cz.larpovadatabaze.components.panel.game.GameBoxPanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;

/**
 * This panel shows information about last added games
 */
public class RecentGamesPanel extends Panel {
    @SpringBean
    GameService gameService;

    private final static int AMOUNT_OF_GAMES = 6;

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
        Collection<Game> recentGames = gameService.getLastGames(AMOUNT_OF_GAMES);
        Iterator<Game> gameIterator = recentGames.iterator();
        for(int i=0; i<6; i++) {
            String id = "recent" + (i + 1);
            if (gameIterator.hasNext()) {
                carousel.add(createGameBox(id, gameIterator.next()));
            }
            else {
                carousel.add(new Label(id, "").setVisible(false));
            }
        }

        // Add most popular games
        List<Game> mostPopularGames = gameService.getMostPopularGames(AMOUNT_OF_GAMES);
        for(int i=0; i<6; i++) {
            String id = "popular" + (i + 1);
            if (recentGames.size() > i) {
                carousel.add(createGameBox(id, mostPopularGames.get(i)));
            }
            else {
                carousel.add(new Label(id, "").setVisible(false));
            }
        }
    }

    /**
     * Create box for the game
     * @param id Component id
     * @param game Game to work on
     * @return Component with the game
     */
    private Component createGameBox(String id, Game game) {
        return new GameBoxPanel(id, Model.of(game));
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
