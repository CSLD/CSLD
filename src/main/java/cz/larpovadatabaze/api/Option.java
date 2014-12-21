package cz.larpovadatabaze.api;

/**
 * Represent Option to contain result or not.
 */
public interface Option<T> {
    T getResult();

    /**
     * Is result present?
     */
    boolean isPresent();
}
