package cz.larpovadatabaze.donations.model;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity(name = "donation")
public class Donation implements cz.larpovadatabaze.common.api.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_donation")
    @SequenceGenerator(sequenceName = "donation_id_seq", name = "id_gen_donation", allocationSize = 1)
    private Integer id;
    private double amount;
    @Temporal(value = TemporalType.DATE)
    private Date date;
    private String donor;
    private String description;

    public Donation(Date date, Double price, String donor, String description) {
        this.donor = donor;
        this.amount = price;
        this.date = date;
        this.description = description;
    }

    public Donation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Donation donation = (Donation) o;

        if (Double.compare(donation.amount, amount) != 0) return false;
        if (id != null ? !id.equals(donation.id) : donation.id != null) return false;
        if (date != null ? !date.equals(donation.date) : donation.date != null) return false;
        return donor != null ? donor.equals(donation.donor) : donation.donor == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (donor != null ? donor.hashCode() : 0);
        return result;
    }
}
