package cz.larpovadatabaze.components.common;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import cz.larpovadatabaze.utils.Strings;

/**
 * User: Michal Kara Date: 28.6.15 Time: 8:39
 */
public class CsldFeedbackMessageLabel extends Label {

    private final String ERROR_CLASS = "error";
    private final String MESSAGE_DIVISOR = "<br />\n";

    private final Component boundComponent;
    private final Component errorClassComponent;
    private final String defaultKey;
    private final IModel<String> classModel = new Model("");

    /**
     * Show message(s) from feedback
     *
     * @param id Component id
     * @param boundComponent Component to show messages from
     * @param errorClassComponent Component to add CSS class "error" on
     * @param defaultKey When there are no messages, show message with this resource key. When NULL and there are no messages, label is hidden
     */
    public CsldFeedbackMessageLabel(String id, Component boundComponent, Component errorClassComponent, String defaultKey) {
        super(id, new Model(""));
        this.boundComponent = boundComponent;
        this.defaultKey = defaultKey;
        this.errorClassComponent = errorClassComponent;
    }

    public CsldFeedbackMessageLabel(String id, Component boundComponent, String defaultKey) {
        this(id, boundComponent, boundComponent, defaultKey);
    }

        @Override
    protected void onInitialize() {
        super.onInitialize();
        setEscapeModelStrings(false); // We do our own escaping

        errorClassComponent.add(new AttributeAppender("class", classModel, " "));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        // Determine message and error state
        if (boundComponent.getFeedbackMessages().isEmpty()) {
            // Default message
            if (defaultKey == null) {
                setVisible(false);
            }
            else {
                setVisible(true);
                ((IModel<String>)getDefaultModel()).setObject(StringEscapeUtils.escapeHtml(Strings.getResourceString(getPage().getClass(), defaultKey)));
            }
        }
        else {
            // Build and set messages
            StringBuilder messages = new StringBuilder();

            classModel.setObject(null); // Not an error
            for(FeedbackMessage msg : boundComponent.getFeedbackMessages()) {
                if (msg.isError() || msg.isFatal()) {
                    // Has error
                    classModel.setObject(ERROR_CLASS);
                }

                if (messages.length() > 0) {
                    // Append divisor
                    messages.append(MESSAGE_DIVISOR);
                }

                // Append message
                messages.append(msg.getMessage());
            }

            setVisible(true);
            ((IModel<String>)getDefaultModel()).setObject(messages.toString());
        }
    }
}
