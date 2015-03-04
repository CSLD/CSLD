package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.icons.GameIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;

/**
 * It shows pageable list of games ordered by time when they were added to the database
 */
public class ListGamesWithAnnotations extends Panel {
    private final int MAX_CHARS_IN_DESCRIPTION = 300;

    @SpringBean
    ImageService imageService;

    public ListGamesWithAnnotations(String id,
                                    SortableDataProvider<Game,String> dataProvider) {
        super(id);
        createListWithAnnotation(dataProvider);
    }

    private void createListWithAnnotation(SortableDataProvider<Game,String> dataProvider){
        DataView<Game> gamesView = new DataView<Game>("gamesView", dataProvider) {
            @Override
            protected void populateItem(Item<Game> item) {
                Game game = item.getModelObject();

                PageParameters gameParams = GameDetail.paramsForGame(game);

                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameIconLink", GameDetail.class, gameParams);
                final GameIcon gameLinkImage = new GameIcon("gameIcon", item.getModel());
                gameLink.add(gameLinkImage);
                item.add(gameLink);

                String gameRatingColor = Rating.getColorOf(game.getAverageRating());
                Label gameRating = new Label("gameRating","");
                gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
                item.add(gameRating);

                final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
                final Label gameName = new Label("gameName", game.getName());
                gameLinkContent.add(gameName);
                item.add(gameLinkContent);

                item.add(new Label("players", Model.of(game.getPlayers())));

                String gameDescription = Jsoup.parse(game.getDescription()).text();
                if (gameDescription.length() > MAX_CHARS_IN_DESCRIPTION) gameDescription = gameDescription.substring(0, MAX_CHARS_IN_DESCRIPTION);
                item.add(new Label("gameDescription", gameDescription));
                final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                        new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, gameParams);
                item.add(gameMoreLink);
            }
        };

        gamesView.setOutputMarkupId(true);
        gamesView.setItemsPerPage(10L);
        add(gamesView);

        add(new PagingNavigator("navigator", gamesView));
    }
}
