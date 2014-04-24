package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.game.ListComments;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This panel shows shortened info about last comments.
 */
public class LastCommentsPanel extends Panel {
    @SpringBean
    CommentService commentService;

    @SpringBean
    ImageService imageService;

    private static final int MAX_CHARS_IN_COMMENT = 150;
    private static int INITIAL_LAST_COMMENTS = 3;
    private static int EXPANDED_LAST_COMMENTS = 10;

    private class CommentsView extends ListView<Comment> {
        public CommentsView(String id, List<? extends Comment> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<Comment> item) {
            Comment comment = item.getModelObject();
            Game game = comment.getGame();
            CsldUser commenter = comment.getUser();

            Fragment commentFragment = new Fragment("comment", "commentFragment", LastCommentsPanel.this);
            item.add(commentFragment);

            PageParameters gameParams = GameDetail.paramsForGame(game);
            PageParameters userParams = new PageParameters();
            userParams.add("id", commenter.getId());

            final BookmarkablePageLink<CsldBasePage> commenterIconLink =
                    new BookmarkablePageLink<CsldBasePage>("commenterIconLink", UserDetail.class, userParams);
            final UserIcon commenterIcon = new UserIcon("commenterIcon", new AbstractReadOnlyModel<CsldUser>() {
                @Override
                public CsldUser getObject() {
                    return item.getModelObject().getUser();
                }
            });
            commenterIconLink.add(commenterIcon);
            commentFragment.add(commenterIconLink);

            final BookmarkablePageLink<CsldBasePage> commenterLink =
                    new BookmarkablePageLink<CsldBasePage>("commenterLink", UserDetail.class, userParams);
            Label commenterName = new Label("commenterName",
                    Model.of(commenter.getPerson().getNickNameView()));
            commenterLink.add(commenterName);
            commentFragment.add(commenterLink);

            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            Date dateOfComment = new Date();
            dateOfComment.setTime(comment.getAdded().getTime());

            Label commentDate = new Label("commentDate", Model.of(formatDate.format(dateOfComment)));
            commentFragment.add(commentDate);

            String gameRatingColor = Rating.getColorOf(game.getTotalRating());
            Label gameRating = new Label("gameRating","");
            gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
            commentFragment.add(gameRating);

            final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                    new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
            final Label gameName = new Label("gameName", game.getName());
            gameLinkContent.add(gameName);
            commentFragment.add(gameLinkContent);

            String commentToShow = Jsoup.parse(comment.getComment()).text();
            if(commentToShow.length() > MAX_CHARS_IN_COMMENT){
                commentToShow = commentToShow.substring(0,MAX_CHARS_IN_COMMENT);
            }
            commentFragment.add(new Label("commentsContent", Model.of(commentToShow)).setEscapeModelStrings(false));
            final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                    new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, gameParams);
            commentFragment.add(gameMoreLink);
        }
    }

    public LastCommentsPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<Comment> toShow = commentService.getLastComments(EXPANDED_LAST_COMMENTS);

        add(new CommentsView("visibleComments", toShow.subList(0, INITIAL_LAST_COMMENTS)));
        add(new CommentsView("hiddenComments", toShow.subList(INITIAL_LAST_COMMENTS, toShow.size())));

        final BookmarkablePageLink<CsldBasePage> allComments =
                new BookmarkablePageLink<CsldBasePage>("allComments", ListComments.class);
        add(allComments);
    }
}
