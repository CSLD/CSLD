package cz.larpovadatabaze.calendar;

import cz.larpovadatabaze.api.Entity;
import org.hibernate.Session;

public class InTransaction {
    private Entity[] entitiesToWorkOn;
    private Session session;

    public InTransaction(Session session, Entity... toStore) {
        entitiesToWorkOn = toStore;
        this.session = session;
    }

    public void store(){
        session.getTransaction().begin();

        for(Entity entity: entitiesToWorkOn) {
            session.persist(entity);
        }

        session.flush();
        session.getTransaction().commit();
    }
}
