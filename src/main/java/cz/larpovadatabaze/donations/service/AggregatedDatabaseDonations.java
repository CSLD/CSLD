package cz.larpovadatabaze.donations.service;

import cz.larpovadatabaze.donations.model.Donation;
import cz.larpovadatabaze.donations.model.Donor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * When retrieving Donation it aggregates these donations based on the person who donated.
 */
public class AggregatedDatabaseDonations implements Donations {
    private Donations donations;

    public AggregatedDatabaseDonations(Donations donations) {
        this.donations = donations;
    }


    @Override
    public void store(Donation donation) {
        donations.store(donation);
    }

    @Override
    public void delete(Donation donation) {
        donations.delete(donation);
    }

    @Override
    public Collection<Donation> all() {
        Collection<Donation> all = donations.all();
        Collection<Donor> aggregated = aggregate(all);

        Collection<Donation> result = new ArrayList<>();
        for(Donor donor: aggregated) {
            result.add(donor.toDonation());
        }
        return result;
    }

    private Collection<Donor> aggregate(Collection<Donation> all) {
        Collection<Donor> aggregated = new ArrayList<>();
        for(Donation donation: all) {
            Donor associatedDonor = new Donor(donation.getDonor());
            if(!aggregated.contains(associatedDonor)){
                associatedDonor.add(donation.getAmount());
                aggregated.add(associatedDonor);
            } else {
                for(Donor donor: aggregated) {
                    if(donor.equals(associatedDonor)) {
                        donor.add(donation.getAmount());
                    }
                }
            }
        }
        return aggregated;
    }
}
