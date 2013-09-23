package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
public class LastGamesPanel extends Panel {
    @SpringBean
    GameService gameService;

    public LastGamesPanel(String id) {
        super(id);

        int AMOUNT_LAST_GAMES = 15;
        List<Game> toShow = gameService.getLastGames(AMOUNT_LAST_GAMES);


        ListView<Game> gamesView = new ListView<Game>("gamesView", toShow) {
            @Override
            protected void populateItem(ListItem<Game> item) {
                Game game = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", game.getId());

                if(game.getImage() == null){
                    game.setImage(cz.larpovadatabaze.entities.Image.getDefaultGame());
                }

                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameIconLink", GameDetail.class, params);
                final Image gameLinkImage = new Image("gameIcon",
                        new ContextRelativeResource(game.getImage().getPath()));
                gameLink.add(gameLinkImage);
                item.add(gameLink);

                String gameRatingColor = Rating.getColorOf(game.getTotalRating());
                Label gameRating = new Label("gameRating","");
                gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
                item.add(gameRating);

                final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, params);
                final Label gameName = new Label("gameName", game.getName());
                gameLinkContent.add(gameName);
                item.add(gameLinkContent);

                item.add(new Label("players", Model.of(game.getPlayers())));
                item.add(new Label("gameDescription",
                        Model.of(
                                game.getDescription().length() > 80 ?
                                        game.getDescription().substring(0,80) :
                                        game.getDescription()))
                );
                final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                        new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, params);
                item.add(gameMoreLink);
            }
        };
        add(gamesView);
    }
}
