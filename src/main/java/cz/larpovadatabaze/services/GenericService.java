package cz.larpovadatabaze.services;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.5.13
 * Time: 8:51
 */
public interface GenericService<T> {
    List<T> getAll();

    List<T> getUnique(T example);

    void remove(T toRemove);
}
