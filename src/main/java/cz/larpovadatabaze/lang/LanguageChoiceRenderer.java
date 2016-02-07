package cz.larpovadatabaze.lang;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.ResourceModel;

/**
 * Renderer for showing languages in human readable way.
 */
public class LanguageChoiceRenderer implements IChoiceRenderer<String> {
    @Override
    public Object getDisplayValue(String object) {
        return new ResourceModel("language."+object).getObject();
    }

    @Override
    public String getIdValue(String object, int index) {
        return object;
    }
}