package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It represents the Events as they are in the Larp.cz page
 */
public class LarpCzEvents {
    private final static Logger logger = Logger.getLogger(LarpCzEvents.class);

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

    private Collection<Event> parseEvents(Collection<Event> result, Elements events) throws IOException {
        int id = 10000; // Get current from database.
        for(Element event: events) {
            LarpCzDate date = new LarpCzDate(event.select("td.views-field-phpcode").get(0).text());
            // Retrieve from and to based on these information.
            String name = event.select("td.views-field-title").get(0).text();
            String detailUrl = event.select("td.views-field-title a").get(0).attr("href");

            Document detailPage = Jsoup.connect("http://www.larp.cz" + detailUrl).get();
            String web = detailPage.select("td.event-meta-value.eweb").get(0).text();
            String description = detailPage.select("div.event-content fieldset").get(0).text();

            Matcher matcher = Pattern.compile("\\d+").matcher(event.select("td.views-field-field-count-value").get(0).text());
            Integer amountOfPlayers = 0;
            if(matcher.find()) {
                amountOfPlayers = Integer.valueOf(matcher.group());
            }
            String loc = event.select("td.views-field-field-region-value").get(0).text() + ", " +
                    event.select("td.views-field-field-city-value").get(0).text();
            Event toAdd = new Event(id, name, date.getFrom(), date.getTo(), amountOfPlayers, loc, description, web, "larpcz");
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
            Collection<SimpleDateFormat>  formatsFrom = new ArrayList<>();
            formatsFrom.add(new SimpleDateFormat("d."));

            Collection<SimpleDateFormat> formatsTo = new ArrayList<>();
            formatsTo.add(new SimpleDateFormat(" d. M. yyyy"));

            from = parse(formatsFrom,parts[0].replace("\u00A0"," "));
            to = parse(formatsTo, parts[1].replace("\u00A0"," "));

            if(to != null) {
                from.setYear(to.getYear());
                from.setMonth(to.getMonth());
            }
        }

        private Date parse(Collection<SimpleDateFormat> formats, String text) {
            for(SimpleDateFormat format: formats) {
                try {
                    return format.parse(text);
                } catch(ParseException ex) {
                    logger.debug("Unparseable date: " + text);
                }
            }
            return null;
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
