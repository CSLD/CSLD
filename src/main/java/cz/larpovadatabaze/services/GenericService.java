package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;

import java.util.List;

/**
 * Necessary minimum every Service must be able to handle.
 */
public interface GenericService<T> {
    List<T> getAll();

    List<T> getUnique(T example);

    void remove(T toRemove);

    List<T> getFirstChoices(String startsWith, int maxChoices);
}
