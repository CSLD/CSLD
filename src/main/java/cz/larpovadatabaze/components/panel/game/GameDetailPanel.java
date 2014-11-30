package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.icons.GameIcon;
import cz.larpovadatabaze.components.common.social.SocialShareButtons;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.group.GroupDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.ImageService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * This panel shows basic information about the game, like its duration, description.
 * amount of players, labels and so on.
 */
public class GameDetailPanel extends AbstractCsldPanel<Game> {
    @SpringBean
    ImageService imageService;

    public GameDetailPanel(String id, IModel<Game> model) {
        super(id, new CompoundPropertyModel<Game>(model));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = getModelObject();

        final GameIcon gameIcon = new GameIcon("gameImage", getModel());
        add(gameIcon);
        add(new Label("name"));

        List<cz.larpovadatabaze.entities.Label> labels = game.getLabels();
        ListView<cz.larpovadatabaze.entities.Label> view = new ListView<cz.larpovadatabaze.entities.Label>("labels", labels) {
            @Override
            protected void populateItem(ListItem<cz.larpovadatabaze.entities.Label> item) {
                cz.larpovadatabaze.entities.Label label = item.getModelObject();
                Label labelC = new Label("label", label.getName());
                item.add(labelC);
                Label tooltip = new Label("tooltip", label.getDescription());
                tooltip.setVisible(StringUtils.isNotBlank(label.getDescription()));
                item.add(tooltip);
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
        add(new Label("lang"));

        add(new ExternalLink("webGameLink", Model.of(game.getWeb()), Model.of(game.getWeb())));

        String galleryURL = game.getGalleryURL();
        ExternalLink webGalleryLink = new ExternalLink("webGalleryLink", Model.of(galleryURL), Model.of(galleryURL));
        webGalleryLink.setVisible(StringUtils.isNotBlank(galleryURL));
        add(webGalleryLink);

        Label photoAuthor = new Label("photoAuthor");
        photoAuthor.setVisible(StringUtils.isNotBlank(game.getPhotoAuthor()));
        add(photoAuthor);

        List<CsldUser> authors = game.getAuthors();
        ListView<CsldUser> authorsList = new ListView<CsldUser>("authors",authors) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                // Add separator
                item.add(new Label("separator", (item.getIndex() == 0)?"":", "));

                // Add author itself
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
                // Add separator
                item.add(new Label("separator", (item.getIndex() == 0)?"":", "));

                // Add group
                CsldGroup group = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", group.getId());

                Link<CsldBasePage> groupDetailLink = new BookmarkablePageLink<CsldBasePage>("authorGroupDetailLink", GroupDetail.class, params);
                groupDetailLink.add(new Label("authorGroupName", group.getName()));
                item.add(groupDetailLink);
            }
        };
        add(groupsList);

        Label description = new Label("description");
        description.setEscapeModelStrings(false);
        add(description);

        add(new SocialShareButtons("socialShareButtons"));
    }
}
