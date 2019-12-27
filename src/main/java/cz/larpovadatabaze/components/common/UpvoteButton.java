package cz.larpovadatabaze.components.common;

import cz.larpovadatabaze.services.AppUsers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The button represents +1 in either the highlighted form or in the non highlighted form.
 * The highlighted form means that the current user has added +1 to the specific item.
 * The non highlighted form means that the current user has the possibility to add +1 to specific item.
 */
public class UpvoteButton extends AbstractCsldPanel<Integer> {
    @SpringBean
    private AppUsers appUsers;

    public UpvoteButton(String id, IModel<Integer> model) {
        super(id, model);
    }

    // There is the linkage to the state. There is something wrong.
    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (!appUsers.isSignedIn()) {
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
        icon.add(new AttributeAppender("class", (IModel<Object>) () ->
                UpvoteButton.this.getModelObject() == 0 ? "far" : "fas", " "));

        button.add(icon);

        add(button);

    }
}
