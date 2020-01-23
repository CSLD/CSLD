package cz.larpovadatabaze.common.services.masqueradeStubs;

import cz.larpovadatabaze.common.Identifiable;
import cz.larpovadatabaze.common.services.CRUDService;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InMemoryCrud<T extends Identifiable, I> implements CRUDService<T, I>, Serializable {
    private static final Logger logger = Logger.getLogger(InMemoryCrud.class);
    protected List<T> inMemory = new ArrayList<>();
    private int idSequence = 0;

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
        if (entity.getId() == null) {
            try {
                Field f1 = entity.getClass().getDeclaredField("id");
                f1.setAccessible(true);
                f1.set(entity, ++idSequence);
                f1.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                logger.warn("The id property doesn't exist for entity: " + entity);
            }
        }

        inMemory.add(entity);

        return true;
    }
}
