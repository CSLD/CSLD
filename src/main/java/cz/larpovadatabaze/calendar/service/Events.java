package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.models.FilterEvent;
import cz.larpovadatabaze.services.CRUDService;
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

    List<Event> geographicallyFiltered(BoundingBox limitation);

    List<Event> byName(String name);
}
