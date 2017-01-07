package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.apache.wicket.request.resource.AbstractResource;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Returns all future events
 */
public class ICalProducerResource extends AbstractResource {
    private SessionFactory sessionFactory;

    public ICalProducerResource(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("text/calendar");
        resourceResponse.setTextEncoding("utf-8");

        System.setProperty("net.fortuna.ical4j.timezone.date.floating", "true");

        Calendar ical = new Calendar();
        ical.getProperties().add(new ProdId("-//CSLD//iCal4j 1.0//EN"));
        ical.getProperties().add(Version.VERSION_2_0);
        ical.getProperties().add(CalScale.GREGORIAN);

        java.util.Calendar to = java.util.Calendar.getInstance();
        to.add(java.util.Calendar.YEAR, 2);

        Collection<Event> eventsToExport = new EventsInTimeFrame(new DatabaseEvents(sessionFactory.getCurrentSession()), java.util.Calendar.getInstance(), to).all();
        for(Event toExport: eventsToExport) {
            ical.getComponents().add(toExport.asIcalEvent());
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
}
