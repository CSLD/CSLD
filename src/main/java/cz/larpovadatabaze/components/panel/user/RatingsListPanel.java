package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * It lists ratings given in as parameter in similar way to games. You get Game info and then your rating of game.
 */
public class RatingsListPanel extends Panel {
    private boolean visible;

    public RatingsListPanel(String id, List<Rating> myRatings, boolean visible) {
        super(id);
        this.visible = visible;

        final ListView<Rating> listView = new ListView<Rating>("listGames", myRatings) {
            @Override
            protected void populateItem(ListItem item) {
                Rating rating = (Rating) item.getModelObject();
                Game game = rating.getGame();

                PageParameters params = new PageParameters();
                params.add("id", game.getId());
                final BookmarkablePageLink<CsldBasePage> gameDetail =
                        new BookmarkablePageLink<CsldBasePage>("gameDetail", GameDetail.class, params);
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

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisibilityAllowed(visible);
    }
}
