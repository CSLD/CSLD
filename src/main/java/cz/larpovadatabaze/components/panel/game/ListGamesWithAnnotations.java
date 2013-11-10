package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * It shows pageable list of games ordered by time when they were added to the database
 */
public class ListGamesWithAnnotations extends Panel {
    private final int MAX_LENGTH = 160;

    public ListGamesWithAnnotations(String id,
                                    SortableDataProvider<Game,String> dataProvider) {
        super(id);
        createListWithAnnotation(dataProvider);
    }

    private void createListWithAnnotation(SortableDataProvider<Game,String> dataProvider){
        Image chartsIcon = new Image("gamesIcon",
                new PackageResourceReference(Csld.class, cz.larpovadatabaze.entities.Image.getChartsIconPath()));
        add(chartsIcon);

        DataView<Game> gamesView = new DataView<Game>("gamesView", dataProvider) {
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
                        new PackageResourceReference(Csld.class, game.getImage().getPath()));
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
