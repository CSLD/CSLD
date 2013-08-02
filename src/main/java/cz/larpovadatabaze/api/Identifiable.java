package cz.larpovadatabaze.api;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 18:14
 */
public interface Identifiable<T extends Serializable>
{
    T getId();
}