package cz.larpovadatabaze.models;

import org.apache.wicket.model.IModel;

/**
 * IModel implementation that allows only get operation. For models that are read only with value derived from another model (or object).
 *
 * User: Michal Kara
 * Date: 20.12.13
 * Time: 12:51
 */
public abstract class ReadOnlyModel<T> implements IModel<T> {
    @Override
    public final void setObject(T object) {
        throw new UnsupportedOperationException(); // Read only model cannot set object
    }

    @Override
    public final void detach() {
        // There is nothing to detach
    }
}
