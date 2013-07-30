package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 30.4.13
 * Time: 13:18
 */
public class GameListPanel extends Panel {
    @SpringBean
    private GameService gameService;

    public GameListPanel(String id, List<Game> games) {
        super(id);

        final ListView<Game> listView = new ListView<Game>("listGames", games) {
            @Override
            protected void populateItem(ListItem item) {
                Game game = (Game) item.getModelObject();
                // TODO ctverecku korektne nastavit barvu.
                // class - great
                // class - average
                // class - mediocre
                String gameRatingColor = "mediocre";
                double gameAsAverageRating = gameService.getRatingOfGame(game);
                if(gameAsAverageRating > 70) {
                    gameRatingColor = "great";
                }
                if(gameAsAverageRating > 40) {
                    gameRatingColor = "average";
                }
                Label gameRating = new Label("gameRating","");
                gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
                item.add(gameRating);

                PageParameters params = new PageParameters();
                params.add("id", game.getId());
                final BookmarkablePageLink<CsldBasePage> gameDetail =
                        new BookmarkablePageLink<CsldBasePage>("gameDetail", GameDetail.class, params);
                final Label gameName = new Label("gameName", game.getName());
                final Label gameYear = new Label("gameYear", game.getYear());
                gameDetail.add(gameName);
                gameDetail.add(gameYear);
                item.add(gameDetail);
            }
        };
        add(listView);

    }
}
