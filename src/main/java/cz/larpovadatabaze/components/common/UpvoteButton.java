package cz.larpovadatabaze.components.common;

import org.apache.wicket.markup.html.basic.Label;
import cz.larpovadatabaze.entities.Upvote;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * The button represents +1 in either the highlighted form or in the non highlighted form.
 * The highlighted form means that the current user has added +1 to the specific item.
 * The non highlighted form means that the current user has the possibility to add +1 to specific item.
 */
public class UpvoteButton extends AbstractCsldPanel<Integer> {
    public UpvoteButton(String id, IModel<Integer> model) {
        super(id, model);
    }

    // There is the linkage to the state. There is something wrong.
    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (!UserUtils.isSignedIn()) {
            // Not visible - do not show at all
            setVisible(false);
            return;
        }

        AjaxLink button = new AjaxLink("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                IModel<Integer> model = UpvoteButton.this.getModel();
                if (model.getObject() > 0) {
                    model.setObject(null);
                } else {
                    model.setObject(1);
                }

                target.add(this);
            }
        };
        button.setOutputMarkupId(true);

        WebMarkupContainer icon = new WebMarkupContainer("icon");
        add(icon);
        icon.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return UpvoteButton.this.getModelObject() == 0 ? "far" : "fas";
            }
        }, " "));

        button.add(icon);

        add(button);

    }
}
