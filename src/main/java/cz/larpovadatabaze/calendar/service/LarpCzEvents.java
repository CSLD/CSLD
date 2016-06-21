package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.utils.Pwd;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.security.provider.MD5;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * It represents the Events as they are in the Larp.cz page
 */
public class LarpCzEvents implements Events {
    private final static Logger logger = Logger.getLogger(LarpCzEvents.class);

    @Override
    public void store(Event event) {
        throw new UnsupportedOperationException("It isn't possible to store events in Larp.cz");
    }

    @Override
    public void delete(Event event) {
        throw new UnsupportedOperationException("It isn't possible to delete events from Larp.cz");
    }

    @Override
    public Collection<Event> all() {
        logger.info("Loading all events form larp.cz page");
        try {
            Collection<Event> events = new ArrayList<>();

            Document firstPage = Jsoup.connect("http://www.larp.cz/kalendar").get();
            Elements eventsToParse = firstPage.select("div.view-content > table > tbody > tr");
            events = parseEvents(events, eventsToParse);

            Document secondPage = Jsoup.connect("http://www.larp.cz/?q=cs/kalendar&page=1").get();
            eventsToParse = secondPage.select("div.view-content > table > tbody > tr");
            events = parseEvents(events, eventsToParse);

            return events;
        } catch (IOException e) {
            throw new RuntimeException("It wasn't possible to download information about Events from larp.cz");
        }
    }

    private Collection<Event> parseEvents(Collection<Event> result, Elements events) {
        int id = 10000;
        for(Element event: events) {
            LarpCzDate date = new LarpCzDate(event.select("td.views-field-phpcode").get(0).text());
            // Retrieve from and to based on these information.
            String name = event.select("td.views-field-title").get(0).text();
            String amountOfPlayers = event.select("td.views-field-field-count-value").get(0).text();
            String loc = event.select("td.views-field-field-region-value").get(0).text() + ", " +
                    event.select("td.views-field-field-city-value").get(0).text();
            Event toAdd = new Event(id, name, date.getFrom(), date.getTo(), amountOfPlayers, loc);
            result.add(toAdd);
            logger.debug("Loaded event: " + toAdd);
            id++;
        }
        return result;
    }

    private class LarpCzDate {
        Date from;
        Date to;

        // TODO: Fix the issue with dates in the LarpCzEvents.
        LarpCzDate(String date) {
            String[] parts = date.split("-");
            if(parts.length < 2) {
                from = new Date();
                to = new Date();
                return;
            }
            SimpleDateFormat larpCzFormatFrom = new SimpleDateFormat("dd.");
            SimpleDateFormat larpCzFormatTo = new SimpleDateFormat(" dd. MM. yyyy");
            try {
                from = larpCzFormatFrom.parse(parts[0]);
                to = larpCzFormatTo.parse(parts[1]);
            } catch (ParseException e) {
                // Ignore. Sometime the value will be wrong.
                e.printStackTrace();
            }
        }

        Calendar getFrom() {
            if(from != null) {
                Calendar result = Calendar.getInstance();
                result.setTime(from);
                return result;
            } else {
                return null;
            }
        }

        Calendar getTo() {
            if(to != null) {
                Calendar result = Calendar.getInstance();
                result.setTime(to);
                return result;
            } else {
                return null;
            }
        }
    }
}
