package cz.larpovadatabaze.components.common;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Abstract panel based on instance of type T. Provides convenience functions to get object / model.
 *
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 13:40
 */
public abstract class AbstractCsldPanel<T> extends Panel {
    public AbstractCsldPanel(String id) {
        super(id);
    }

    public AbstractCsldPanel(String id, IModel<T> model) {
        super(id, model);
    }

    protected IModel<T> getModel() {
        return (IModel<T>)getDefaultModel();
    }

    protected T getModelObject() {
        return (T)getDefaultModelObject();
    }
}
