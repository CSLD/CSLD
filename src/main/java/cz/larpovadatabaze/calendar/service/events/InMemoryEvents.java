package cz.larpovadatabaze.calendar.service.events;

import cz.larpovadatabaze.calendar.BoundingBox;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.models.FilterEvent;
import cz.larpovadatabaze.services.masqueradeStubs.InMemoryCrud;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InMemoryEvents extends InMemoryCrud<Event, Integer> implements Events {
    @Override
    public List<Event> forWantedGames(CsldUser user, List<Event> events) {
        return new ArrayList<>();
    }

    @Override
    public List<Event> inTheTimeFrame(Calendar from, Calendar to) {
        return new ArrayList<>();
    }

    @Override
    public List<Event> filtered(IModel<FilterEvent> filter) {
        return new ArrayList<>();
    }

    @Override
    public List<Event> geographicallyFiltered(BoundingBox limitation) {
        return new ArrayList<>();
    }

    @Override
    public List<Event> byName(String name) {
        return new ArrayList<>();
    }
}
