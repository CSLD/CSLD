package cz.larpovadatabaze.donations.service;

import cz.larpovadatabaze.donations.model.Donation;

import java.util.Collection;

/**
 * Service for manipulating donations
 */
public interface Donations {
    /**
     * Store Donation into persistent store, for example database.
     * @param donation Donation to store into persistent store.
     */
    void store(Donation donation);

    /**
     * Remove instance with given id if present. If it isn't present it shouldn't do anything.
     * @param donation Donation to be removed from persistent storage.
     */
    void delete(Donation donation);

    /**
     * It returns all donations in current store.
     * @return Donations stored in current store.
     */
    Collection<Donation> all();
}
