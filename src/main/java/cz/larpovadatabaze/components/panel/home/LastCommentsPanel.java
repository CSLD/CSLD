package cz.larpovadatabaze.components.panel.home;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CommentService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class LastCommentsPanel extends Panel {
    @SpringBean
    CommentService commentService;
    private final int MAX_CHARS_IN_COMMENT = 80;

    public LastCommentsPanel(String id) {
        super(id);

        int AMOUNT_LAST_COMMENTS = 15;
        List<Comment> toShow = commentService.getLastComments(AMOUNT_LAST_COMMENTS);
        ListView<Comment> gamesView = new ListView<Comment>("commentsView", toShow) {
            @Override
            protected void populateItem(ListItem<Comment> item) {
                Comment comment = item.getModelObject();
                Game game = comment.getGame();
                CsldUser commenter = comment.getUser();

                PageParameters gameParams = new PageParameters();
                gameParams.add("id", game.getId());
                PageParameters userParams = new PageParameters();
                userParams.add("id", commenter.getId());

                if(commenter.getImage() == null){
                    commenter.setImage(cz.larpovadatabaze.entities.Image.getDefaultUser());
                }

                final BookmarkablePageLink<CsldBasePage> commenterIconLink =
                        new BookmarkablePageLink<CsldBasePage>("commenterIconLink", UserDetail.class, userParams);
                final Image commenterIcon = new Image("commenterIcon",
                        new ContextRelativeResource(commenter.getImage().getPath()));
                commenterIconLink.add(commenterIcon);
                item.add(commenterIconLink);

                final BookmarkablePageLink<CsldBasePage> commenterLink =
                        new BookmarkablePageLink<CsldBasePage>("commenterLink", UserDetail.class, userParams);
                Label commenterName = new Label("commenterName",
                        Model.of(commenter.getPerson().getNickname() + " - " +  commenter.getPerson().getName()));
                commenterLink.add(commenterName);
                item.add(commenterLink);

                SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
                Date dateOfComment = new Date();
                dateOfComment.setTime(comment.getAdded().getTime());

                Label commentDate = new Label("commentDate", Model.of(formatDate.format(dateOfComment)));
                item.add(commentDate);

                String gameRatingColor = Rating.getColorOf(game.getTotalRating());
                Label gameRating = new Label("gameRating","");
                gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
                item.add(gameRating);

                final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
                final Label gameName = new Label("gameName", game.getName());
                gameLinkContent.add(gameName);
                item.add(gameLinkContent);

                String commentToShow = comment.getComment();
                if(commentToShow.length() > MAX_CHARS_IN_COMMENT){
                    commentToShow = commentToShow.substring(0,MAX_CHARS_IN_COMMENT);
                }
                item.add(new Label("commentsContent", Model.of(commentToShow)).setEscapeModelStrings(false));
                final BookmarkablePageLink<CsldBasePage> gameMoreLink =
                        new BookmarkablePageLink<CsldBasePage>("gameMoreLink", GameDetail.class, gameParams);
                item.add(gameMoreLink);
            }
        };
        add(gamesView);
    }
}
