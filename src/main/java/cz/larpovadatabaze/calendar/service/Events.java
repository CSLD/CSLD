package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.services.CRUDService;
import org.apache.wicket.model.IModel;

import java.util.Calendar;
import java.util.List;

/**
 * Service for manipulating events.
 */
public interface Events extends CRUDService<Event, Integer> {
    List<Event> forWantedGames(CsldUser user, List<Event> events);

    List<Event> inTheTimeFrame(Calendar from, Calendar to);

    List<Event> filtered(IModel<FilterEvent> filter);

    List<Event> byName(String name);

    /**
     * Replace who added the events added by specific user by nobody
     *
     * @param toRemove User whose events we need to clean.
     */
    void removeAddedBy(CsldUser toRemove);
}

