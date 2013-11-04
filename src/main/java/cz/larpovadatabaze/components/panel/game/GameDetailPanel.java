package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.author.AuthorDetail;
import cz.larpovadatabaze.components.page.group.GroupDetail;
import cz.larpovadatabaze.components.panel.YouTubePanel;
import cz.larpovadatabaze.components.panel.photo.ManagePhotoPanel;
import cz.larpovadatabaze.components.panel.photo.ShowPhotoPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.basic.Label;
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
 * This panel shows basic information about the game, like its duration, description.
 * amount of players, labels and so on.
 */
public class GameDetailPanel extends Panel {
    public GameDetailPanel(String id, Game game) {
        super(id);


        final Image adminImage = new Image("gameImage",
                new ContextRelativeResource((game.getImage() != null) ? game.getImage().getPath(): cz.larpovadatabaze.entities.Image.getDefaultGame().getPath()));
        add(adminImage);
        Label gameName =
                new Label("gameName", game.getName());
        add(gameName);

        List<cz.larpovadatabaze.entities.Label> labels = game.getLabels();
        ListView<cz.larpovadatabaze.entities.Label> view = new ListView<cz.larpovadatabaze.entities.Label>("labels", labels) {
            @Override
            protected void populateItem(ListItem<cz.larpovadatabaze.entities.Label> item) {
                cz.larpovadatabaze.entities.Label label = item.getModelObject();
                item.add(new Label("label", label.getName()));
            }
        };
        add(view);

        Label players = new Label("players", game.getPlayers());
        add(players);
        Label men = new Label("men", game.getMenRole());
        add(men);
        Label women = new Label("women", game.getWomenRole());
        add(women);
        Label both = new Label("both", game.getBothRole());
        add(both);

        Label hours = new Label("hours", game.getHours());
        add(hours);
        Label days = new Label("days", game.getDays());
        add(days);
        Label years = new Label("year", game.getYear());
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
                        new Label("authorName", author.getPerson().getNickname() + " " + author.getPerson().getName()));
                item.add(authorDetailLink);
            }
        };
        add(authorsList);

        List<CsldGroup> groups = game.getGroupAuthor();
        ListView<CsldGroup> groupsList = new ListView<CsldGroup>("authorsGroups",groups) {
            @Override
            protected void populateItem(ListItem<CsldGroup> item) {
                CsldGroup group = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", group.getId());

                Link<CsldBasePage> groupDetailLink = new BookmarkablePageLink<CsldBasePage>("authorGroupDetailLink", GroupDetail.class, params);
                groupDetailLink.add(
                        new Label("authorGroupName", group.getName()));
                item.add(groupDetailLink);
            }
        };
        add(groupsList);

        Label description = new Label("description",game.getDescription());
        description.setEscapeModelStrings(false);
        add(description);

        String videoPath = "";
        boolean isVisible = true;
        if(game.getVideo() == null){
            isVisible = false;
        } else {
            videoPath = game.getVideo().getPath();
        }

        YouTubePanel youTubePanel = new YouTubePanel("video", videoPath, isVisible);
        add(youTubePanel);

        ShowPhotoPanel photoPanel = new ShowPhotoPanel("photos", game.getPhotos());
        add(photoPanel);

        CsldUser loggedUser = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        boolean show = false;
        if(loggedUser != null){
            if(game.getAuthors().contains(loggedUser)) {
                show = true;
            }
            if(loggedUser.getRole() >= CsldRoles.ADMIN.getRole()) {
                show = true;
            }
        }
        // Administrators
        ManagePhotoPanel managePhotoPanel = new ManagePhotoPanel("managePhotos", game.getPhotos(), show, game);
        add(managePhotoPanel);
    }
}
