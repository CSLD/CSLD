package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.IGameWithRating;
import cz.larpovadatabaze.games.components.page.GameDetail;
import cz.larpovadatabaze.games.services.Ratings;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * It lists ratings given in as parameter in similar way to games. You get Game info and then your rating of game.
 */
public class RatingsListPanel extends Panel {
    @SpringBean
    private Ratings ratings;

    private final IModel<List<IGameWithRating>> model;

    public RatingsListPanel(String id, IModel<List<IGameWithRating>> model) {
        super(id);
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ListView<IGameWithRating> listView = new ListView<IGameWithRating>("listGames", model) {
            @Override
            protected void populateItem(ListItem item) {
                IGameWithRating rating = (IGameWithRating) item.getModelObject();
                Game game = rating.getGame();

                WebMarkupContainer gameRating = new WebMarkupContainer("gameRating");
                gameRating.add(new AttributeAppender("class", Model.of(ratings.getColorForGame(game)), " "));
                item.add(gameRating);

                final BookmarkablePageLink<CsldBasePage> gameDetail =
                        new BookmarkablePageLink<CsldBasePage>("gameDetail", GameDetail.class, GameDetail.paramsForGame(game));
                final Label gameName = new Label("gameName", game.getName());
                final Label gameYear = new Label("gameYear", game.getYear());
                gameDetail.add(gameName);
                gameDetail.add(gameYear);
                item.add(gameDetail);

                final Label myRating = new Label("myRating", Model.of(rating.getRating()));
                item.add(myRating);
            }
        };
        add(listView);
    }
}
