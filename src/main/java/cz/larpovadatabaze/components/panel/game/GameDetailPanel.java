package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.author.AuthorDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 14.5.13
 * Time: 10:00
 */
public class GameDetailPanel extends Panel {
    public GameDetailPanel(String id, Game game) {
        super(id);


        final Image adminImage = new Image("gameImage",
                new ContextRelativeResource((game.getImage() != null) ? game.getImage().getPath(): ""));
        add(adminImage);
        org.apache.wicket.markup.html.basic.Label gameName =
                new org.apache.wicket.markup.html.basic.Label("gameName", game.getName());
        add(gameName);

        List<Label> labels = game.getLabels();
        ListView<Label> view = new ListView<Label>("labels", labels) {
            @Override
            protected void populateItem(ListItem<Label> item) {
                Label label = item.getModelObject();
                item.add(new org.apache.wicket.markup.html.basic.Label("label", label.getName()));
            }
        };
        add(view);

        org.apache.wicket.markup.html.basic.Label players =
                new org.apache.wicket.markup.html.basic.Label("players", game.getPlayers());
        add(players);
        org.apache.wicket.markup.html.basic.Label men =
                new org.apache.wicket.markup.html.basic.Label("men", game.getMenRole());
        add(men);
        org.apache.wicket.markup.html.basic.Label women =
                new org.apache.wicket.markup.html.basic.Label("women", game.getWomenRole());
        add(women);
        org.apache.wicket.markup.html.basic.Label both =
                new org.apache.wicket.markup.html.basic.Label("both", game.getBothRole());
        add(both);

        org.apache.wicket.markup.html.basic.Label hours =
                new org.apache.wicket.markup.html.basic.Label("hours", game.getHours());
        add(hours);
        org.apache.wicket.markup.html.basic.Label days =
                new org.apache.wicket.markup.html.basic.Label("days", game.getDays());
        add(days);
        org.apache.wicket.markup.html.basic.Label years =
                new org.apache.wicket.markup.html.basic.Label("year", game.getYear());
        add(years);

        add(new ExternalLink("webGameLink", game.getWeb(), game.getWeb()));

        List<CsldUser> authors = game.getAuthors();
        ListView<CsldUser> authorsList = new ListView<CsldUser>("authors",authors) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser author = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", author.getId());

                Link<CsldBasePage> authorDetailLink = new BookmarkablePageLink<CsldBasePage>("authorDetailLink", AuthorDetail.class, params);
                authorDetailLink.add(
                        new org.apache.wicket.markup.html.basic.Label("authorName", author.getPerson().getName()));
                item.add(authorDetailLink);
            }
        };
        add(authorsList);

        org.apache.wicket.markup.html.basic.Label description = new org.apache.wicket.markup.html.basic.Label("description",game.getDescription());
        add(description);
    }
}
