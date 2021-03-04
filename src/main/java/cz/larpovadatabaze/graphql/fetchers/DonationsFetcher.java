package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.donations.model.Donation;
import cz.larpovadatabaze.donations.service.AggregatedDatabaseDonations;
import cz.larpovadatabaze.donations.service.DatabaseDonations;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DonationsFetcher implements DataFetcher<Collection<Donation>> {
    private final SessionFactory sessionFactory;

    @Autowired
    public DonationsFetcher(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Collection<Donation> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        Collection<Donation> res = new AggregatedDatabaseDonations(new DatabaseDonations(sessionFactory.getCurrentSession())).all();
        return res;
    }
}
