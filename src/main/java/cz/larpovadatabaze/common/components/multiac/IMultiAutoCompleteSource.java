package cz.larpovadatabaze.common.components.multiac;

import cz.larpovadatabaze.common.api.Identifiable;

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
