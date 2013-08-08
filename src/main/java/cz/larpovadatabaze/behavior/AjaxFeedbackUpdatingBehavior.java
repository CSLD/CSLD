package cz.larpovadatabaze.behavior;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * It allows adding ajax messages to feedback panels belonging to every input.
 */
public class AjaxFeedbackUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {
    private FeedbackPanel feedback;

    /**
     * Construct.
     *
     * @param event event to trigger this behavior
     */
    public AjaxFeedbackUpdatingBehavior(String event,
                                        FeedbackPanel feedback) {
        super(event);

        this.feedback = feedback;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        target.add(feedback);
    }

    @Override
    protected void onError(AjaxRequestTarget target, RuntimeException ex){
        super.onError(target,ex);
        target.add(feedback);
    }
}
