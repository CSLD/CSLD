package cz.larpovadatabaze.donations.model;

/**
 * It represents one Donor. Mainly used internally.
 */
public class Donor {
    private double amount;
    private String donor;

    public Donor(String name) {
        this.donor = name;
        amount = 0;
    }

    public void add(double amount) {
        this.amount += amount;
    }

    public Donation toDonation() {
        Donation donation = new Donation();
        donation.setAmount(amount);
        donation.setDonor(donor);
        return donation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Donor donor1 = (Donor) o;

        return donor.equals(donor1.donor);
    }

    @Override
    public int hashCode() {
        return donor.hashCode();
    }
}
