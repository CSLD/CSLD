package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.CommentHiddenButton;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Upvote;
import cz.larpovadatabaze.games.components.UpvoteButton;
import cz.larpovadatabaze.games.components.page.GameDetail;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.games.services.Upvotes;
import cz.larpovadatabaze.users.components.icons.UserIcon;
import cz.larpovadatabaze.users.components.page.UserDetailPage;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * It contains all games in a pageable list, there are four possible ways to order
 * the list. Order alphabetically, Order by rating or order by amount of ratings, or
 * by amount of comments.
 */
public abstract class AbstractListCommentPanel<T> extends AbstractCsldPanel<T> {
    @SpringBean
    Comments comments;
    @SpringBean
    Ratings ratings;
    @SpringBean
    AppUsers appUsers;
    @SpringBean
    Upvotes upvotes;

    private SortableDataProvider<Comment, String> sdp;
    private boolean showGame;

    /**
     * Model to be used for handling the Upvotes.
     */
    private final class UpvoteModel implements IModel<Integer> {
        /**
         * Cached comment
         */
        private Comment actualComment;
        private Integer commentId;

        private UpvoteModel(Comment comment) {
            actualComment = comment;
            commentId = comment.getId();
        }

        private CsldUser getUser() {
            return appUsers.getLoggedUser();
        }

        @Override
        public Integer getObject() {
            return upvotes.forUserAndComment(getUser(), getActualComment()).size();
        }

        private Comment getActualComment() {
            if (actualComment == null) {
                actualComment = comments.getById(commentId);
            }

            return actualComment;
        }

        @Override
        public void setObject(Integer amountOfVotes) {
            if (amountOfVotes == null) {
                upvotes.downvote(getUser(), getActualComment());
            } else {
                for (int i = 0; i < amountOfVotes; i++) {
                    upvotes.upvote(getUser(), getActualComment());
                }
            }
        }

        @Override
        public void detach() {
            actualComment = null;
        }
    }

    public AbstractListCommentPanel(String id, boolean showGame) {
        super(id);

        this.showGame = showGame;
    }

    /**
     * @return Data provider
     */
    protected abstract SortableDataProvider<Comment, String> getDataProvider();

    @Override
    protected void onInitialize() {
        super.onInitialize();

        sdp = getDataProvider();
        final DataView<Comment> propertyList = new DataView<>("listComments", sdp) {
            @Override
            protected void populateItem(Item<Comment> item) {
                Comment actualComment = item.getModelObject();
                SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
                Date dateOfComment = new Date();
                dateOfComment.setTime(actualComment.getAdded().getTime());
                List<Upvote> pluses = actualComment.getPluses();
                item.add(new Label("pluses", Model.of(pluses.size())));
                item.add(new UpvoteButton("upvote", new UpvoteModel(actualComment)));

                // Hide comment button
                item.add(new CommentHiddenButton("commentHiddenButton", item.getModel()));

                // Author link
                CsldUser authorOfComment = actualComment.getUser();
                PageParameters params = new PageParameters();
                params.add("id", authorOfComment.getId());
                final BookmarkablePageLink<CsldBasePage> authorLink =
                        new BookmarkablePageLink<>("authorLink", UserDetailPage.class, params);
                item.add(authorLink);

                // Author image
                final UserIcon authorsAvatar = new UserIcon("avatar", (IModel<CsldUser>) () ->
                        item.getModelObject().getUser());
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
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(10L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }

    private WebMarkupContainer createGameDetailLink(Game game) {
        WebMarkupContainer toShow = new WebMarkupContainer("showGame");
        String gameRatingColor = ratings.getColor(game.getAverageRating());
        Label gameRating = new Label("gameRating", "");
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
