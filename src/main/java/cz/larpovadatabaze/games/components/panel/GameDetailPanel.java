package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.social.SocialShareButtons;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.components.page.ListGamePage;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.components.page.GroupDetail;
import cz.larpovadatabaze.users.components.page.UserDetailPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

import java.util.ArrayList;
import java.util.List;

/**
 * This panel shows basic information about the game, like its duration, description.
 * amount of players, labels and so on.
 */
public class GameDetailPanel extends AbstractCsldPanel<Game> {
    @SpringBean
    Images images;

    public GameDetailPanel(String id, IModel<Game> model) {
        super(id, new CompoundPropertyModel<>(model));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = getModelObject();

        // Name
        add(new Label("name"));

        // Players etc
        add(new Label("players"));
        add(new Label("menRole"));
        add(new Label("womenRole"));
        add(new Label("bothRole"));

        // How long the game is
        add(new Label("hours"));
        add(new Label("days"));
        add(new Label("year"));

        // Web link
        add(new ExternalLink("webGameLink", Model.of(game.getWeb()), Model.of(game.getWeb())).setVisible(StringUtils.isNotBlank(game.getWeb())));
        // Add new link to the blueprint.
        add(new ExternalLink("bluePrintLink", Model.of(game.getBlueprintPath()), Model.of(game.getBlueprintPath()))
                .setVisible(StringUtils.isNotBlank(game.getWeb())));

        // Gallery
        String galleryURL = game.getGalleryURL();
        ExternalLink webGalleryLink = new ExternalLink("webGalleryLink", Model.of(galleryURL), Model.of(galleryURL));
        webGalleryLink.setVisible(StringUtils.isNotBlank(galleryURL));
        add(webGalleryLink);

        // Photo author
        Label photoAuthor = new Label("photoAuthor");
        photoAuthor.setVisible(StringUtils.isNotBlank(game.getPhotoAuthor()));
        add(photoAuthor);

        // Authors
        List<CsldUser> authors = game.getAuthors();
        ListView<CsldUser> authorsList = new ListView<>("authors", authors) {
            @Override
            protected void populateItem(ListItem<CsldUser> item) {
                // Add separator
                item.add(new Label("separator", (item.getIndex() == 0) ? "" : ", "));

                // Add author itself
                CsldUser author = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", author.getId());

                Link<CsldBasePage> authorDetailLink = new BookmarkablePageLink<>("authorDetailLink", UserDetailPage.class, params);
                authorDetailLink.add(
                        new Label("authorName", author.getPerson().getNickNameView() + " " + author.getPerson().getName()));
                item.add(authorDetailLink);

            }
        };
        add(authorsList);

        // Groups
        List<CsldGroup> groups = game.getGroupAuthor();
        if(groups == null) {
            groups = new ArrayList<>();
        }
        ListView<CsldGroup> groupsList = new ListView<>("authorsGroups", groups) {
            @Override
            protected void populateItem(ListItem<CsldGroup> item) {
                // Add separator
                item.add(new Label("separator", (item.getIndex() == 0) ? "" : ", "));

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
        add(new WebMarkupContainer("dash").setVisible(groups.isEmpty())); // Dash when groups are empty

        // Description
        Label description = new Label("description");
        description.setEscapeModelStrings(false);
        add(description);

        // Social share button
        add(new SocialShareButtons("socialShareButtons"));

        // Labels
        List<cz.larpovadatabaze.common.entities.Label> labels = game.getLabels();
        ListView<cz.larpovadatabaze.common.entities.Label> view = new ListView<>("labels", labels) {
            @Override
            protected void populateItem(ListItem<cz.larpovadatabaze.common.entities.Label> item) {
                cz.larpovadatabaze.common.entities.Label label = item.getModelObject();

                BookmarkablePageLink link = new BookmarkablePageLink<CsldBasePage>("link", ListGamePage.class, ListGamePage.getParametersForLabel(label.getId()));
                item.add(link);
                if (StringUtils.isNotEmpty(label.getDescription())) {
                    link.add(new AttributeAppender("title", label.getDescription()));
                }

                Label labelC = new Label("label", label.getName());
                link.add(labelC);
            }
        };
        add(view);
    }
}
