package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.services.CRUDService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InMemoryCrud<T extends Identifiable, I> implements CRUDService<T, I>, Serializable {
    protected List<T> inMemory = new ArrayList<>();

    @Override
    public List<T> getAll() {
        return inMemory;
    }

    @Override
    public List<T> getUnique(T example) {
        List<T> uniqueOnesByExample = new ArrayList<>();
        for (T entity : inMemory) {
            if (entity.equals(example)) {
                uniqueOnesByExample.add(entity);
            }
        }

        return uniqueOnesByExample;
    }

    @Override
    public T getById(I id) {
        for (T entity : inMemory) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }

        return null;
    }

    @Override
    public void remove(T toRemove) {
        List<T> forRemoval = new ArrayList<>();
        for (T entity : inMemory) {
            if (entity.equals(toRemove)) {
                forRemoval.add(entity);
            }
        }

        for (T remove : forRemoval) {
            inMemory.remove(remove);
        }
    }

    @Override
    public List<T> getFirstChoices(String startsWith, int maxChoices) {
        return null;
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        inMemory.add(entity);

        return true;
    }
}
