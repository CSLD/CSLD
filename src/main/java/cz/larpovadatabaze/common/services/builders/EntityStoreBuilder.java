package cz.larpovadatabaze.common.services.builders;

import cz.larpovadatabaze.common.entities.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Supporting class, which allows user to create and store entities.
 */
@Service
public class EntityStoreBuilder {
    @Autowired
    private SessionFactory persistenceStore;

    public CsldUser user(CsldUser toSave) {
        save(toSave);
        return toSave;
    }

    public CsldGroup group(CsldGroup toSave) {
        save(toSave);
        return toSave;
    }

    public Label label(Label toSave) {
        save(toSave);
        return toSave;
    }

    public Game game(Game toSave) {
        save(toSave);
        return toSave;
    }

    public Rating rating(Rating toSave) {
        save(toSave);
        return toSave;
    }

    public Comment comment(Comment toSave) {
        save(toSave);
        return toSave;
    }

    public Upvote plusOne(Upvote toSave) {
        save(toSave);
        return toSave;
    }

    public UserPlayedGame playerOfGame(UserPlayedGame toSave) {
        save(toSave);
        return toSave;
    }

    public SimilarGame similarGame(SimilarGame toSave) {
        save(toSave);
        return toSave;
    }

    public void flush() {
        persistenceStore.getCurrentSession().flush();
    }

    /**
     * To be changed in order to use JPA EntityManager.
     *
     * @param o Object to save in persistent store. Basically it is either JPA entity or hibernate entity.
     */
    private void save(Object o) {
        persistenceStore.getCurrentSession().save(o);
    }
}