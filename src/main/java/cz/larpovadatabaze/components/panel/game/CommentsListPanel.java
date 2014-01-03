package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This Panel shows List of comments. Everything about comment is shown and the
 * full text of the comment.
 */
public class CommentsListPanel extends Panel {
    @SpringBean
    ImageService imageService;

    private final IModel<List<Comment>> comments;

    private final boolean showGame;

    public CommentsListPanel(String id, IModel<List<Comment>> comments) {
        this(id, comments, false);
    }

    public CommentsListPanel(String id, IModel<List<Comment>> comments, final boolean showGame) {
        super(id);

        this.comments = comments;
        this.showGame = showGame;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ListView<Comment> commentList = new ListView<Comment>("commentList", comments) {
            @Override
            protected void populateItem(final ListItem<Comment> item) {
                Comment actualComment = item.getModelObject();
                SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
                Date dateOfComment = new Date();
                dateOfComment.setTime(actualComment.getAdded().getTime());

                Label commentDate = new Label("commentDate", Model.of(formatDate.format(dateOfComment)));
                item.add(commentDate);
                Label commentsContent = new Label("commentsContent", Model.of(actualComment.getComment()));
                commentsContent.setEscapeModelStrings(false);
                item.add(commentsContent);

                CsldUser authorOfComment = actualComment.getUser();

                final UserIcon authorsAvatar = new UserIcon("authorsAvatar", new AbstractReadOnlyModel<CsldUser>() {
                    @Override
                    public CsldUser getObject() {
                        return item.getModelObject().getUser();
                    }
                });
                item.add(authorsAvatar);

                PageParameters params = new PageParameters();
                params.add("id", authorOfComment.getId());
                final BookmarkablePageLink<CsldBasePage> commentAuthorsDetail =
                        new BookmarkablePageLink<CsldBasePage>("authorsDetail", UserDetail.class, params);
                Label authorsNick = new Label("authorsNick", Model.of(authorOfComment.getPerson().getNickNameView()));
                Label authorsName = new Label("authorsName", Model.of(authorOfComment.getPerson().getName()));

                commentAuthorsDetail.add(authorsNick);
                commentAuthorsDetail.add(authorsName);

                WebMarkupContainer gameLabel = createGameDetailLink(actualComment.getGame());
                gameLabel.setVisibilityAllowed(showGame);
                item.add(gameLabel);

                item.add(commentAuthorsDetail);
            }
        };
        add(commentList);
    }

    private WebMarkupContainer createGameDetailLink(Game game) {
        WebMarkupContainer toShow = new WebMarkupContainer("showGame");
        String gameRatingColor = Rating.getColorOf(game.getTotalRating());
        Label gameRating = new Label("gameRating","");
        gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
        toShow.add(gameRating);

        PageParameters params = new PageParameters();
        params.add("id", game.getId());
        final BookmarkablePageLink<CsldBasePage> gameDetail =
                new BookmarkablePageLink<CsldBasePage>("gameDetail", GameDetail.class, params);
        final Label gameName = new Label("gameName", game.getName());
        gameDetail.add(gameName);
        toShow.add(gameDetail);
        return toShow;
    }
}
