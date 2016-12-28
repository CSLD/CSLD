package cz.larpovadatabaze.donations.service;

import cz.larpovadatabaze.donations.model.Donation;
import org.hibernate.Session;

import java.util.Collection;

/**
 * It stores the data in the database.
 */
public class DatabaseDonations implements Donations {
    private Session session;

    public DatabaseDonations(Session session) {
        this.session = session;
    }

    @Override
    public void store(Donation donation) {
        Donation toSave = donation;
        if(!session.contains(toSave)) {
            toSave = (Donation) session.merge(toSave);
        }
        session.persist(toSave);
        session.flush();  // TODO: Understand why the persist doesn't work. Probably unfinished transaction.

        donation.setId(toSave.getId());
    }

    @Override
    public void delete(Donation donation) {
        session.delete(donation);
    }

    @Override
    public Collection<Donation> all() {
        return session.createCriteria(Donation.class).list();
    }
}
