package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.services.CsldUserService;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.util.string.StringValue;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Returns all future events
 */
public class ICalProducerResource extends AbstractResource {
    private SessionFactory sessionFactory;
    private CsldUserService userService;

    public ICalProducerResource(SessionFactory sessionFactory, CsldUserService userService) {
        this.sessionFactory = sessionFactory;
        this.userService = userService;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        StringValue id = attributes.getParameters().get("id");

        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("text/calendar");
        resourceResponse.setTextEncoding("utf-8");

        Calendar ical = new Calendar();
        ical.getProperties().add(new ProdId("-//CSLD//iCal4j 1.0//EN"));
        ical.getProperties().add(Version.VERSION_2_0);
        ical.getProperties().add(CalScale.GREGORIAN);

        java.util.Calendar to = java.util.Calendar.getInstance();
        to.add(java.util.Calendar.YEAR, 2);

        if(id.isEmpty() || id.toString().startsWith("all")) {
            addAllEvents(ical, to);
        } else {
            addEventsForUser(id, ical, to);
        }

        if(ical.getComponents().size() == 0) {
            java.util.Calendar inPast = java.util.Calendar.getInstance();
            inPast.set(java.util.Calendar.YEAR, 2010);
            ical.getComponents().add(new VEvent(new net.fortuna.ical4j.model.Date(inPast), "Irelevant"));
        }

        resourceResponse.setWriteCallback(new WriteCallback()
        {
            @Override
            public void writeData(Attributes attributes) throws IOException
            {
                CalendarOutputter outputter = new CalendarOutputter();
                outputter.output(ical, attributes.getResponse().getOutputStream());
            }
        });

        return resourceResponse;
    }

    private void addAllEvents(Calendar ical, java.util.Calendar to) {
        Collection<Event> eventsToExport = new EventsInTimeFrame(new DatabaseEvents(sessionFactory.getCurrentSession()), java.util.Calendar.getInstance(), to).all();
        for (Event toExport : eventsToExport) {
            ical.getComponents().add(toExport.asIcalEvent());
        }
    }

    private void addEventsForUser(StringValue id, Calendar ical, java.util.Calendar to) {
// TODO: Sanitize for incorrect usage.
        int userId = id.toInt();
        Collection<Event> eventsToExport = new EventsForWantedGames(userService.getById(userId), new EventsInTimeFrame(new DatabaseEvents(sessionFactory.getCurrentSession()), java.util.Calendar.getInstance(), to)).all();
        for (Event toExport : eventsToExport) {
            ical.getComponents().add(toExport.asIcalEvent());
        }
    }
}
