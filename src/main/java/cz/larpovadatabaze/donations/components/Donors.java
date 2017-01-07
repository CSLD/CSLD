package cz.larpovadatabaze.donations.components;

import cz.larpovadatabaze.donations.model.Donation;
import cz.larpovadatabaze.donations.service.AggregatedDatabaseDonations;
import cz.larpovadatabaze.donations.service.DatabaseDonations;
import org.apache.wicket.behavior.AttributeAppender;
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

                Label name = new Label("name", Model.of(donation.getDescription()));
                Label amount = new Label("amount", Model.of(donation.getAmount()));

                if(donation.getAmount() >= 3000) {
                    name.add(new AttributeAppender("class", "gold"));
                    amount.add(new AttributeAppender("class", "gold"));
                } else if (donation.getAmount() >= 1000) {
                    name.add(new AttributeAppender("class", "silver"));
                    amount.add(new AttributeAppender("class", "silver"));
                } else {
                    name.add(new AttributeAppender("class", "bronze"));
                    amount.add(new AttributeAppender("class", "bronze"));
                }

                item.add(name);
                item.add(amount);
            }
        });
    }
}
