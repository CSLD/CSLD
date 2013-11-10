package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This Panel shows List of comments. Everything about comment is shown and the
 * full text of the comment.
 */
public class CommentsListPanel extends Panel {
    private List<Comment> comments;

    public CommentsListPanel(String id, List<Comment> comments) {
        this(id, comments, false);
    }

    public CommentsListPanel(String id, List<Comment> comments, final boolean showGame) {
        super(id);

        if(comments == null){
            comments = new ArrayList<Comment>();
        }
        this.comments = comments;

        ListView<Comment> commentList = new ListView<Comment>("commentList", comments) {
            @Override
            protected void populateItem(ListItem<Comment> item) {
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

                final Image authorsAvatar = new Image("authorsAvatar",
                        new PackageResourceReference(Csld.class, authorOfComment.getImage().getPath()));
                item.add(authorsAvatar);

                PageParameters params = new PageParameters();
                params.add("id", authorOfComment.getId());
                final BookmarkablePageLink<CsldBasePage> commentAuthorsDetail =
                        new BookmarkablePageLink<CsldBasePage>("authorsDetail", UserDetail.class, params);
                Label authorsNick = new Label("authorsNick", Model.of(authorOfComment.getPerson().getNickname()));
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

    public void reload(AjaxRequestTarget target, List<Comment> comments) {
        this.comments.removeAll(this.comments);
        this.comments.addAll(comments);

        target.add(this);
    }
}
