package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.detail.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 30.4.13
 * Time: 13:17
 */
public class AuthorDetailPanel extends Panel {
    @SpringBean
    private GameService gameService;

    public AuthorDetailPanel(String id, Integer authorId) {
        super(id);

        List<Game> games = gameService.getGamesOfAuthor(authorId);
        final ListView<Game> gamesList = new ListView<Game>("", games) {
            @Override
            protected void populateItem(ListItem<Game> item) {
                Game game = item.getModelObject();
                cz.larpovadatabaze.entities.Image image = (game.getImage() != null) ?
                        game.getImage(): new cz.larpovadatabaze.entities.Image();
                final Image ctverecHra =
                        new Image("ctverecHra", new ContextRelativeResource(image.getPath()));
                item.add(ctverecHra);

                PageParameters params = new PageParameters();
                params.add("gameId",game.getId());
                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class);
                final Label gameName = new Label("gameName", game.getName());
                final Label gameYear = new Label("gameYear", game.getImage().getPath());
                item.add(gameName);
                item.add(gameYear);
                item.add(gameLink);
            }
        };
    }
}
