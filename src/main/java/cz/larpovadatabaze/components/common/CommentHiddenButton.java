package cz.larpovadatabaze.components.common;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 4.1.14
 * Time: 23:00
 */
public class CommentHiddenButton extends AbstractCsldPanel<Comment> {
    @SpringBean
    private CommentService commentService;

    public CommentHiddenButton(String id, IModel<Comment> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Determine if we are visible
        if (!UserUtils.isEditor()) {
            // Not visible - do not show at all
            setVisible(false);
            return;
        }

        AjaxLink button = new AjaxLink("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Comment comment = CommentHiddenButton.this.getModelObject();

                if (Boolean.TRUE.equals(comment.getHidden())) commentService.unHideComment(comment);
                else commentService.hideComment(comment);

                target.add(this);
            }
        };
        button.setOutputMarkupId(true);

        button.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return Boolean.TRUE.equals(CommentHiddenButton.this.getModelObject().getHidden())?"hidden":"";
            }
        }," "));

        add(button);
    }
}
