package cz.larpovadatabaze.lang.components;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;

/**
 * Popup for choosing languages.
 */
public class ChooseLanguages extends ModalWindow {


    public ChooseLanguages(String id, IModel<?> model) {
        super(id, model);
    }
}
