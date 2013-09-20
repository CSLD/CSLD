package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default List of all Comments belonging to given game.
 */
public class CommentsListPanel extends Panel {
    private List<Comment> comments;

    public CommentsListPanel(String id, List<Comment> comments) {
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
                        new ContextRelativeResource(authorOfComment.getImage().getPath()));
                item.add(authorsAvatar);

                PageParameters params = new PageParameters();
                params.add("id", authorOfComment.getId());
                final BookmarkablePageLink<CsldBasePage> commentAuthorsDetail =
                        new BookmarkablePageLink<CsldBasePage>("authorsDetail", UserDetail.class, params);
                Label authorsNick = new Label("authorsNick", Model.of(authorOfComment.getPerson().getNickname()));
                Label authorsName = new Label("authorsName", Model.of(authorOfComment.getPerson().getName()));

                commentAuthorsDetail.add(authorsNick);
                commentAuthorsDetail.add(authorsName);
                item.add(commentAuthorsDetail);
            }
        };
        add(commentList);
    }

    public void reload(AjaxRequestTarget target, List<Comment> comments) {
        this.comments.removeAll(this.comments);
        this.comments.addAll(comments);

        target.add(this);
    }
}
