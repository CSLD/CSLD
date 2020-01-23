package cz.larpovadatabaze.common;

import java.io.Serializable;

/**
 * All entities needs to be identifiable.
 */
public interface Identifiable<T extends Serializable>
{
    T getId();
}