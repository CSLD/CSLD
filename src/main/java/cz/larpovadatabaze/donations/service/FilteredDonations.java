package cz.larpovadatabaze.donations.service;

import cz.larpovadatabaze.donations.model.Donation;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.Date;

/**
 * Used to decide whether given donation already exist. It is rather improbable that the payment with the same amount
 * and person will be payed during the same day.
 */
public class FilteredDonations implements Donations {
    private Session session;
    private Donations database;

    private String name;
    private Date date;
    private Double amount;

    public FilteredDonations(Session session, String name, Date date, Double amount) {
        this.session = session;
        database = new DatabaseDonations(session);

        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    @Override
    public void store(Donation donation) {
        database.store(donation);
    }

    @Override
    public void delete(Donation donation) {
        database.delete(donation);
    }

    @Override
    public Collection<Donation> all() {
        return session.createCriteria(Donation.class)
                .add(Restrictions.and(
                        Restrictions.eq("donor", name),
                        Restrictions.eq("date", date),
                        Restrictions.eq("amount", amount)
                )).list();
    }
}
