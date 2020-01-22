package cz.larpovadatabaze.common.api;

import java.io.Serializable;

/**
 * All entities needs to be identifiable.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 18:14
 */
public interface Identifiable<T extends Serializable>
{
    T getId();
}