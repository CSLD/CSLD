package cz.larpovadatabaze.common.components;

import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.games.services.Comments;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 4.1.14
 * Time: 23:00
 */
public class CommentHiddenButton extends AbstractCsldPanel<Comment> {
    @SpringBean
    private Comments comments;
    @SpringBean
    private AppUsers appUsers;

    public CommentHiddenButton(String id, IModel<Comment> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Determine if we are visible
        if (!appUsers.isEditor()) {
            // Not visible - do not show at all
            setVisible(false);
            return;
        }

        AjaxLink button = new AjaxLink("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Comment comment = CommentHiddenButton.this.getModelObject();

                if (Boolean.TRUE.equals(comment.getHidden())) comments.unHideComment(comment);
                else comments.hideComment(comment);

                target.add(this);
            }
        };
        button.setOutputMarkupId(true);

        WebMarkupContainer icon = new WebMarkupContainer("icon");
        add(icon);
        icon.add(new AttributeAppender("class", (IModel<Object>) () ->
                Boolean.TRUE.equals(CommentHiddenButton.this.getModelObject().getHidden()) ?
                        "fa-eye-slash" : "fa-eye", " "));

        button.add(icon);

        add(button);
    }
}
