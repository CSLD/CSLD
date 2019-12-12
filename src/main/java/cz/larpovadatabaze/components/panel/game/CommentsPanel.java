package cz.larpovadatabaze.components.panel.game;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This panel allows user to Comment given game
 */
public class CommentsPanel extends Panel {
    private final String VARIATION_DISABLED = "disabled";
    private final String VARIATION_EDIT = "edit";

    @SpringBean
    CommentService commentService;

    private WysiwygEditor commentContent;

    private final IModel<Game> gameModel;

    private final CommentTextModel model;

    private final Component[] refreshOnChange;

    /**
     * Model for comment text. Works with the complete comment, which it caches
     * (and allows detaching).
     */
    private class CommentTextModel implements IModel<String> {

        /**
         * Id of game
         */
        private int gameId;

        /**
         * Cached comment
         */
        private Comment actualComment;

        private CommentTextModel(int gameId) {
            this.gameId = gameId;
        }

        private CsldUser getUser() {
            return UserUtils.getLoggedUser();
        }

        private int getUserId(CsldUser user) {
            return (user == null) ? -1 : user.getId();
        }

        private void loadIfNecessary() {
            if (actualComment == null) {
                // Load comment
                CsldUser user = getUser();
                int userId = getUserId(user);

                actualComment = commentService.getCommentOnGameFromUser(userId, gameId);
                if (actualComment == null) {
                    // Init comment
                    actualComment = new Comment();
                    actualComment.setUser(user);
                    actualComment.setGame(gameModel.getObject());
                    actualComment.setComment("");
                }
            }
        }

        @Override
        public String getObject() {
            loadIfNecessary();
            return actualComment.getComment();
        }

        @Override
        public void setObject(String newComment) {
            loadIfNecessary();

            if (!actualComment.getComment().equals(newComment)) {
                // Comment changed - save
                if (newComment == null || newComment.equals("")) {
                    try {
                        commentService.remove(actualComment);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    actualComment.setComment(Jsoup.clean(newComment, Whitelist.basic()));
                    if (actualComment.getAdded() == null) {
                        actualComment.setAdded(new Timestamp(System.currentTimeMillis()));
                    }

                    if(actualComment.getPluses() == null) {
                        actualComment.setPluses(new ArrayList<>());
                    }

                    commentService.saveOrUpdate(actualComment);
                }
            }
        }

        @Override
        public void detach() {
            actualComment = null;
        }
    }

    /**
     * Constructor
     *
     * @param id              Panel id
     * @param gameModel       Model of the game comments are for
     * @param refreshOnChange Components to refresh on change
     */
    public CommentsPanel(String id, IModel<Game> gameModel, Component[] refreshOnChange) {
        super(id);
        this.model = new CommentTextModel(gameModel.getObject().getId());
        this.gameModel = gameModel;
        this.refreshOnChange = refreshOnChange;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (VARIATION_EDIT.equals(getVariation())) {
            ValidatableForm<Comment> commentForm = new ValidatableForm<Comment>("comment") {
            };
            commentForm.setOutputMarkupId(true);

            // Wysiwyg // Replacement for TinyMCE
            DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbar");
            final WysiwygEditor editor = new WysiwygEditor("textOfComment", model, toolbar);

            final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
            commentForm.add(feedback);
            commentForm.add(toolbar, editor);

            AjaxButton addComment = new AjaxButton("addComment") {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    // Reload game
                    gameModel.detach();

                    // Refresh panels
                    target.add(this);
                    target.add(refreshOnChange);
                }
            };
            addComment.setOutputMarkupId(true);

            commentForm.add(addComment);

            add(commentForm);
        }
    }

    protected void onConfigure() {
        super.onConfigure();

        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    @Override
    public String getVariation() {
        return (gameModel.getObject().isCommentsDisabled() && StringUtils.isEmpty(model.getObject())) ? VARIATION_DISABLED : VARIATION_EDIT;
    }

    protected void onCsldAction(AjaxRequestTarget target) {
    }
}
