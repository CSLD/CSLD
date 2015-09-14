package cz.larpovadatabaze.components.common.multiac;

import cz.larpovadatabaze.api.Identifiable;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: Michal Kara Date: 27.6.15 Time: 15:03
 */
public interface IMultiAutoCompleteSource<T extends Identifiable & IAutoCompletable> extends Serializable {
    /**
     * Get choices for autocomplete
     *
     * @param input User input
     *
     * @return Choices matching input
     */
    public Collection<T> getChoices(String input);

    /**
     * Get object by ID
     *
     * @param id ID
     * @return Object
     */
    public T getObjectById(Long id);
}
