package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.users.services.CsldUsers;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns all future events
 */
@RestController
public class ICalProducerResource {
    private Events events;
    private CsldUsers userService;

    public ICalProducerResource(Events events, CsldUsers userService) {
        this.events = events;
        this.userService = userService;
    }

    @GetMapping(path="/ical")
    protected void newResourceResponse(HttpServletRequest request, HttpServletResponse response, @RequestParam String id) {
        response.setContentType("text/calendar");

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

        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(ical, response.getOutputStream());
        } catch (ValidationException|IOException e) {
            response.setStatus(500);
        } 
    }

    private void addAllEvents(Calendar ical, java.util.Calendar to) {
        Collection<Event> eventsToExport = events.inTheTimeFrame(java.util.Calendar.getInstance(), to);
        for (Event toExport : eventsToExport) {
            ical.getComponents().add(toExport.asIcalEvent());
        }
    }

    private void addEventsForUser(String id, Calendar ical, java.util.Calendar to) {
        int userId = Integer.parseInt(id);
        Collection<Event> eventsToExport = events.forWantedGames(userService.getById(userId),
                events.inTheTimeFrame(java.util.Calendar.getInstance(), to));
        for (Event toExport : eventsToExport) {
            ical.getComponents().add(toExport.asIcalEvent());
        }
    }
}
