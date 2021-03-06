package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.services.CRUDService;

import java.util.List;

/**
 *
 */
public interface Labels extends CRUDService<Label, Integer> {
    /**
     * Returns all required labels.
     *
     * @return List of the labels, that are among required.
     */
    List<Label> getRequired();

    /**
     * Returns all optional labels. Optional label doesn't suffice as only label for the game.
     *
     * @return List of the labels, that are optional.
     */
    List<Label> getOptional();

    /**
     * Returns those optional labels, which the specific user has access rights towards.
     *
     * @param authorizedTo User requesting the labels
     * @return List of the optional labels further limited by the Access rights of the given user.
     */
    List<Label> getAuthorizedOptional(CsldUser authorizedTo);

    /**
     * Returns those required labels, which the specific user has access rights towards.
     *
     * @param authorizedTo User requesting the labels.
     * @return List of the required labels further limited by the Access rights of the given user.
     */
    List<Label> getAuthorizedRequired(CsldUser authorizedTo);

    /**
     * Replace who added the labels added by specific user by nobody
     *
     * @param toRemove User whose labels we need to clean.
     */
    void removeAddedBy(CsldUser toRemove);
}
