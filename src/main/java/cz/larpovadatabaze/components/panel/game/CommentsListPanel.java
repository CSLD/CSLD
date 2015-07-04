package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.larpovadatabaze.components.common.CommentHiddenButton;
import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.ImageService;

/**
 * This Panel shows List of comments. Everything about comment is shown and the
 * full text of the comment.
 */
public class CommentsListPanel extends Panel {
    @SpringBean
    ImageService imageService;

    @SpringBean
    CommentService commentService;

    private final IModel<List<Comment>> comments;

    private final boolean showGame;

    /**
     * User in the list view, always gets comment from DB
     */
    private final class CommentModel extends LoadableDetachableModel<Comment> {
        private final int gameId;
        private final int userId;

        private CommentModel(int gameId, int userId) {
            this.gameId = gameId;
            this.userId = userId;
        }

        @Override
        protected Comment load() {
            return commentService.getCommentOnGameFromUser(userId, gameId);
        }
    }

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
            protected IModel<Comment> getListItemModel(IModel<? extends List<Comment>> listViewModel, int index) {
                // We want to always keep items pointing to the same comment
                Comment c = listViewModel.getObject().get(index);
                return new CommentModel(c.getGameId(), c.getUserId());
            }

            @Override
            protected void populateItem(final ListItem<Comment> item) {
                Comment actualComment = item.getModelObject();
                SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
                Date dateOfComment = new Date();
                dateOfComment.setTime(actualComment.getAdded().getTime());

                // Hide comment button
                item.add(new CommentHiddenButton("commentHiddenButton", item.getModel()));

                // Author link
                CsldUser authorOfComment = actualComment.getUser();
                PageParameters params = new PageParameters();
                params.add("id", authorOfComment.getId());
                final BookmarkablePageLink<CsldBasePage> authorLink =
                    new BookmarkablePageLink<CsldBasePage>("authorLink", UserDetailPage.class, params);
                item.add(authorLink);

                // Author image
                final UserIcon authorsAvatar = new UserIcon("avatar", new AbstractReadOnlyModel<CsldUser>() {
                    @Override
                    public CsldUser getObject() {
                        return item.getModelObject().getUser();
                    }
                });
                authorLink.add(authorsAvatar);

                // Author nick && name
                authorLink.add(new Label("nick", Model.of(authorOfComment.getPerson().getNickNameView())));
                authorLink.add(new Label("name", Model.of(authorOfComment.getPerson().getName())));

                // Date
                Label commentDate = new Label("commentDate", Model.of(formatDate.format(dateOfComment)));
                item.add(commentDate);

                // Content
                Label commentsContent = new Label("commentsContent", Model.of(actualComment.getComment()));
                commentsContent.setEscapeModelStrings(false);
                item.add(commentsContent);

                WebMarkupContainer gameLabel = createGameDetailLink(actualComment.getGame());
                gameLabel.setVisibilityAllowed(showGame);
                item.add(gameLabel);
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

        final BookmarkablePageLink<CsldBasePage> gameDetail =
                new BookmarkablePageLink<CsldBasePage>("gameDetail", GameDetail.class, GameDetail.paramsForGame(game));
        final Label gameName = new Label("gameName", game.getName());
        gameDetail.add(gameName);
        toShow.add(gameDetail);
        return toShow;
    }
}
