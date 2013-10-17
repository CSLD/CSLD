package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.10.13
 * Time: 15:57
 */
public class ListLastGames extends CsldBasePage {
    @SpringBean
    private GameService gameService;
    private final int MAX_LENGTH = 160;

    public ListLastGames(){
        Image chartsIcon = new Image("gamesIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getChartsIconPath()));
        add(chartsIcon);

        SortableGameProvider sgp = new SortableGameProvider(gameService, "added");
        DataView<Game> gamesView = new DataView<Game>("gamesView", sgp) {
            @Override
            protected void populateItem(Item<Game> item) {
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
                                game.getDescription().length() > MAX_LENGTH ?
                                        game.getDescription().substring(0,MAX_LENGTH) :
                                        game.getDescription()))
                );
                final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                        new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, params);
                item.add(gameMoreLink);
            }
        };

        gamesView.setOutputMarkupId(true);
        gamesView.setItemsPerPage(10L);
        add(gamesView);

        add(new PagingNavigator("navigator", gamesView));
    }
}
