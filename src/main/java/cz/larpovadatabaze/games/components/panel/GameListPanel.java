package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.components.page.GameDetail;
import cz.larpovadatabaze.games.services.Ratings;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * It lists games given in parameter as square with color based on the rating of game followed by its name and year
 * when it was created.
 */
public class GameListPanel extends Panel {
    @SpringBean
    Ratings ratings;

    private final IModel<List<Game>> gamesToDisplay;

    public GameListPanel(String id, IModel<List<Game>> gamesToDisplay) {
        super(id);
        this.gamesToDisplay = gamesToDisplay;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (gamesToDisplay.getObject() != null) {
            List<Game> gamesList = gamesToDisplay.getObject().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            gamesToDisplay.setObject(gamesList);
        }

        final ListView<Game> listView = new ListView<Game>("listGames", gamesToDisplay) {
            @Override
            protected void populateItem(ListItem item) {
                Game game = (Game) item.getModelObject();
                String gameRatingColor = ratings.getColor(game.getAverageRating());
                Label gameRating = new Label("gameRating", "");
                gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
                item.add(gameRating);

                final BookmarkablePageLink<CsldBasePage> gameDetail =
                        new BookmarkablePageLink<CsldBasePage>("gameDetail", GameDetail.class, GameDetail.paramsForGame(game));
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
