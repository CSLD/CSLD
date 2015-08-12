package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.sql.Timestamp;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CommentService;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

/**
 * This panel allows user to Comment given game
 */
public class CommentsPanel extends Panel {
    private final String VARIATION_DISABLED = "disabled";
    private final String VARIATION_EDIT = "edit";

        @SpringBean
    CommentService commentService;

    private TextArea<String> commentContent;

    private final IModel<Game> gameModel;

    private final CommentTextModel model;

    private final Component[] refreshOnChange;

    private LanguageSolver sessionLanguageSolver = new SessionLanguageSolver();

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
            return ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        }

        private int getUserId(CsldUser user) {
            return (user == null)?-1:user.getId();
        }

        private void loadIfNecessary() {
            if (actualComment == null) {
                // Load comment
                CsldUser user = getUser();
                int userId = getUserId(user);

                actualComment = commentService.getCommentOnGameFromUser(userId, gameId);
                if(actualComment == null) {
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
                if(newComment == null || newComment.equals("")){
                    try{
                        commentService.remove(actualComment);
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                } else {
                    actualComment.setLang(sessionLanguageSolver.getTextLangForUser().get(0));
                    actualComment.setComment(Jsoup.clean(newComment, Whitelist.basic()));
                    if (actualComment.getAdded() == null) {
                        actualComment.setAdded(new Timestamp(System.currentTimeMillis()));
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
     * @param id Panel id
     * @param gameModel Model of the game comments are for
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
            ValidatableForm<Comment> commentForm = new ValidatableForm<Comment>("comment"){};
            commentForm.setOutputMarkupId(true);

            commentContent = new TextArea<String>("textOfComment", model);
            commentContent.add(new CSLDTinyMceBehavior());
            commentContent.setOutputMarkupId(true);
            AjaxButton addComment = new AjaxButton("addComment"){
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    // Reload game
                    gameModel.detach();

                    // Refresh panels
                    target.add(this);
                    target.add(refreshOnChange);
                }
            };
            addComment.setOutputMarkupId(true);
            addComment.add(new TinyMceAjaxSubmitModifier());

            commentForm.add(commentContent);
            commentForm.add(addComment);

            add(commentForm);
        }
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }

    @Override
    public String getVariation() {
        return (gameModel.getObject().isCommentsDisabled() && StringUtils.isEmpty(model.getObject()))? VARIATION_DISABLED : VARIATION_EDIT;
    }

    protected void onCsldAction(AjaxRequestTarget target){}
}
