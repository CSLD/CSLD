package cz.larpovadatabaze.tags.service;

import cz.larpovadatabaze.entities.Label;
import org.hibernate.Session;

import java.util.Collection;

/**
 * It stores tags in the persistent store.
 */
public class DatabaseTags implements Tags {
    private Session session;

    public DatabaseTags(Session session) {
        this.session = session;
    }

    @Override
    public Collection<Label> all() {
        return session.createCriteria(Label.class).list();
    }
}
