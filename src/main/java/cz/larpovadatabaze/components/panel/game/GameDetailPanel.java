package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.GroupDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

/**
 * This panel shows basic information about the game, like its duration, description.
 * amount of players, labels and so on.
 */
public class GameDetailPanel extends Panel {
    private final IModel<Game> model;

    public GameDetailPanel(String id, IModel<Game> model) {
        super(id);
        this.model = model;
        setDefaultModel(new CompoundPropertyModel<Game>(model));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = model.getObject();

        final Image gameIcon = new Image("gameImage",
                new PackageResourceReference(Csld.class, (game.getImage() != null) ? game.getImage().getPath(): cz.larpovadatabaze.entities.Image.getDefaultGame().getPath()));
        add(gameIcon);
        add(new Label("name"));

        List<cz.larpovadatabaze.entities.Label> labels = game.getLabels();
        ListView<cz.larpovadatabaze.entities.Label> view = new ListView<cz.larpovadatabaze.entities.Label>("labels", labels) {
            @Override
            protected void populateItem(ListItem<cz.larpovadatabaze.entities.Label> item) {
                cz.larpovadatabaze.entities.Label label = item.getModelObject();
                item.add(new Label("label", label.getName()));
            }
        };
        add(view);

        add(new Label("players"));
        add(new Label("menRole"));
        add(new Label("womenRole"));
        add(new Label("bothRole"));

        add(new Label("hours"));
        add(new Label("days"));
        add(new Label("year"));

        add(new ExternalLink("webGameLink", Model.of(game.getWeb()), Model.of(game.getWeb())));

        List<CsldUser> authors = game.getAuthors();
        ListView<CsldUser> authorsList = new ListView<CsldUser>("authors",authors) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                CsldUser author = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", author.getId());

                Link<CsldBasePage> authorDetailLink = new BookmarkablePageLink<CsldBasePage>("authorDetailLink", UserDetail.class, params);
                authorDetailLink.add(
                        new Label("authorName", author.getPerson().getNickNameView() + " " + author.getPerson().getName()));
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

        Label description = new Label("description");
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
        ManagePhotoPanel managePhotoPanel = new ManagePhotoPanel("managePhotos", game.getPhotos(), show, model);
        add(managePhotoPanel);
    }
}
