package cz.larpovadatabaze.components.panel.home;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.ImageService;

/**
 * This panel shows shortened info about last comments.
 */
public class LastCommentsPanel extends Panel {
    @SpringBean
    CommentService commentService;

    @SpringBean
    ImageService imageService;

    private static final int MAX_CHARS_IN_COMMENT = 150;
    private static int INITIAL_LAST_COMMENTS = 6;
    private static int EXPANDED_LAST_COMMENTS = 15;

    private class CommentsView extends ListView<Comment> {
        public CommentsView(String id, List<? extends Comment> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<Comment> item) {
            Comment comment = item.getModelObject();
            Game game = comment.getGame();
            CsldUser commenter = comment.getUser();

            // Create fragment
            Fragment f = new Fragment("comment", "commentFragment", LastCommentsPanel.this);
            item.add(f);

            // Content
            String commentToShow = Jsoup.parse(comment.getComment()).text();
            if(commentToShow.length() > MAX_CHARS_IN_COMMENT){
                commentToShow = commentToShow.substring(0,MAX_CHARS_IN_COMMENT);
            }
            f.add(new Label("commentsContent", Model.of(commentToShow)).setEscapeModelStrings(false));

            // User icon
            final UserIcon commenterIcon = new UserIcon("commenterIcon", new AbstractReadOnlyModel<CsldUser>() {
                @Override
                public CsldUser getObject() {
                    return item.getModelObject().getUser();
                }
            });
            f.add(commenterIcon);

            // Link and name
            PageParameters userParams = new PageParameters();
            userParams.add("id", commenter.getId());
            final BookmarkablePageLink<CsldBasePage> commenterLink =
                new BookmarkablePageLink<CsldBasePage>("commenterLink", UserDetail.class, userParams);
            Label commenterName = new Label("commenterName", Model.of(commenter.getPerson().getNickNameView()));
            commenterLink.add(commenterName);
            f.add(commenterLink);

            // Game link
            PageParameters gameParams = GameDetail.paramsForGame(game);
            final BookmarkablePageLink<CsldBasePage> gameLink = new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
            f.add(gameLink);

            // Game rating
            String gameRatingColor = Rating.getColorOf(game.getTotalRating());
            Label gameRating = new Label("gameRating","");
            gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
            gameLink.add(gameRating);

            // Game name
            final Label gameName = new Label("gameName", game.getName());
            gameLink.add(gameName);

            // Game date
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            Date dateOfComment = new Date();
            dateOfComment.setTime(comment.getAdded().getTime());
            Label commentDate = new Label("commentDate", Model.of(formatDate.format(dateOfComment)));
            f.add(commentDate);
        }
    }

    public LastCommentsPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<Comment> toShow = commentService.getLastComments(EXPANDED_LAST_COMMENTS);

        if(toShow.size() >= INITIAL_LAST_COMMENTS) {
            add(new CommentsView("visibleComments", toShow.subList(0, INITIAL_LAST_COMMENTS)));
            add(new CommentsView("hiddenComments", toShow.subList(INITIAL_LAST_COMMENTS, toShow.size())));
        } else {
            add(new CommentsView("visibleComments", toShow));
            add(new CommentsView("hiddenComments", new ArrayList<Comment>()));
        }
    }
}
