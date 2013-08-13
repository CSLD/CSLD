package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.IListener;
import cz.larpovadatabaze.api.IPublisher;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CommentService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This panel allows user to Comment given game
 */
public abstract class CommentsPanel extends Panel {
    @SpringBean
    CommentService commentService;

    private Comment actualComment;
    private TextArea<String> commentContent;

    /**
     * Game must be non null value.
     *
     * @param id
     * @param game This value can not be null.
     */
    public CommentsPanel(String id, final Game game) {
        super(id);

        // Null is valid value of logged. Be careful.
        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        int userId = (logged != null) ? logged.getId() : -1;

        actualComment = commentService.getCommentOnGameFromUser(userId, game.getId());
        String commentText = "";
        if(actualComment == null) {
            actualComment = new Comment();
            actualComment.setUserId(userId);
            actualComment.setGameId(game.getId());
            actualComment.setComment("");
        } else {
            commentText = actualComment.getComment();
        }

        ValidatableForm<Comment> comment = new ValidatableForm<Comment>("comment"){};
        comment.setOutputMarkupId(true);

        commentContent = new TextArea<String>("textOfComment", Model.of(commentText));
        commentContent.setOutputMarkupId(true);
        AjaxButton addComment = new AjaxButton("addComment"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saveComment();
                onCsldAction(target,form);
            }
        };
        addComment.setOutputMarkupId(true);

        comment.add(commentContent);
        comment.add(addComment);

        add(comment);
    }

    /**
     * Some Components might want to show Comment as early as it is added.
     */
    private void saveComment() {
        String commentText = commentContent.getModelObject();
        actualComment.setComment(commentText);
        actualComment.setAdded(new Timestamp(System.currentTimeMillis()));
        commentService.saveOrUpdate(actualComment);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
