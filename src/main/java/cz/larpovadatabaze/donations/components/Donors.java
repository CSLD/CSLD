package cz.larpovadatabaze.donations.components;

import cz.larpovadatabaze.donations.model.Donation;
import cz.larpovadatabaze.donations.service.AggregatedDatabaseDonations;
import cz.larpovadatabaze.donations.service.DatabaseDonations;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Panel showing all the donors with information about them.
 */
public class Donors extends Panel {
    @SpringBean
    private SessionFactory sessionFactory;

    public Donors(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Collection<Donation> donations = new AggregatedDatabaseDonations(new DatabaseDonations(sessionFactory.getCurrentSession())).all();

        add(new ListView<Donation>("donors", new ArrayList<>(donations)) {
            @Override
            protected void populateItem(ListItem<Donation> item) {
                Donation donation = item.getModelObject();

                item.add(new Label("name", Model.of(donation.getDonor())));
                item.add(new Label("amount", Model.of(donation.getAmount())));
            }
        });
    }
}
