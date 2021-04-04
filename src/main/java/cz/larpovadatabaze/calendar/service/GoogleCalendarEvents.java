package cz.larpovadatabaze.calendar.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.common.entities.Label;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Component
public class GoogleCalendarEvents {
    private final static Logger logger = Logger.getLogger(GoogleCalendarEvents.class);

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String APPLICATION_NAME = "LARP Database";
    private static final String CALENDAR_ID = "i72qpc43a8pmoqsl42oqo9og4s@group.calendar.google.com";
    /*"3a28s0se1pq6qs76fpmftldsr8@group.calendar.google.com"*/

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSX");

    private final Events events;

    public static class SyncStats {
        private final int gCalEvents;
        private final int ldEvents;
        private int eventsCreated;
        private int eventsUpdated;

        private SyncStats(int gCalEvents, int ldEvents) {
            this.gCalEvents = gCalEvents;
            this.ldEvents = ldEvents;
        }

        public void incCreated() {
            this.eventsCreated++;
        }

        public void incUpdated() {
            this.eventsUpdated++;
        }

        public int getGCalEvents() {
            return gCalEvents;
        }

        public int getLdEvents() {
            return ldEvents;
        }

        public int getEventsCreated() {
            return eventsCreated;
        }

        public int getEventsUpdated() {
            return eventsUpdated;
        }

        @Override
        public String toString() {
            return "Google Calendar sync stats: created: " + eventsCreated + ", updated: " + eventsUpdated + ", gCalEvents: " + gCalEvents + ", ldEvents: " + ldEvents;
        }
    }

    @Autowired
    public GoogleCalendarEvents(Events events) {
        this.events = events;
    }

    private Date eventDateTimeToDate(EventDateTime eventDate, boolean isEndDate) {
        String date;
        boolean subtractDay = false;
        String timeZone = eventDate.getTimeZone();

        if (timeZone == null) {
            // Use default time zone when not specified
            timeZone = "Europe/Prague";
        }

        if (eventDate.getDateTime() != null) {
            date = eventDate.getDateTime().toString();
        } else {
            date = eventDate.getDate().toString() + "T00:00:00";

            // When we have just dates, we must subtract one day on the end date
            // because LD and GCal interpret these differently
            subtractDay = isEndDate;
        }

        // Cut off TZ index because it cannot be parsed - timezone will be added later anyway
        if (date.length() > 23) {
            date = date.substring(0, 23);
        }

        ZonedDateTime zdt = LocalDateTime.parse(date).atZone(ZoneId.of(timeZone));
        Instant instant = zdt.toInstant();
        if (subtractDay) {
            instant = instant.minus(1, ChronoField.DAY_OF_YEAR.getBaseUnit());
        }
        return Date.from(instant);
    }

    private boolean isMidnight(java.util.Calendar cal) {
        return cal.get(GregorianCalendar.HOUR) == 0 && cal.get(GregorianCalendar.MINUTE) == 0 && cal.get(GregorianCalendar.SECOND) == 0 && cal.get(GregorianCalendar.MILLISECOND) == 0;
    }

    private Pair<EventDateTime, EventDateTime> dateToEventDateTime(java.util.Calendar startCal, java.util.Calendar endCal) {
        EventDateTime gCalStart = new EventDateTime();
        EventDateTime gCalEnd = new EventDateTime();

        gCalStart.setTimeZone("Europe/Prague");
        gCalEnd.setTimeZone("Europe/Prague");

        boolean wholeDay = isMidnight(startCal) && isMidnight(endCal);
        if (wholeDay) {
            gCalStart.setDate(new DateTime(startCal.getTime(), TimeZone.getTimeZone("Europe/Prague")));

            ZonedDateTime zdt = ZonedDateTime.of(endCal.get(GregorianCalendar.YEAR), endCal.get(GregorianCalendar.MONTH), endCal.get(GregorianCalendar.DATE), 0, 0, 0, 0, ZoneId.of("Europe/Prague"));
            zdt = zdt.plus(1, ChronoField.DAY_OF_YEAR.getBaseUnit());
            gCalEnd.setDate(new DateTime(new Date(zdt.toInstant().toEpochMilli()), TimeZone.getTimeZone("Europe/Prague")));
        } else {
            gCalStart.setDateTime(new DateTime(startCal.getTime(), TimeZone.getTimeZone("Europe/Prague")));
            gCalEnd.setDateTime(new DateTime(endCal.getTime(), TimeZone.getTimeZone("Europe/Prague")));
        }

        return Pair.of(gCalStart, gCalEnd);
    }

    private void updateEventWithGCalData(Event ldEvent, com.google.api.services.calendar.model.Event gcEvent) {
        ldEvent.setGCalEventId(gcEvent.getId());
        ldEvent.setGCalEventLastUpdated(gcEvent.getUpdated().getValue());
        ldEvent.setName(gcEvent.getSummary());
        ldEvent.setFrom(eventDateTimeToDate(gcEvent.getStart(), false));
        ldEvent.setTo(eventDateTimeToDate(gcEvent.getEnd(), true));

        // Strip of description at may "="s - in case we created that event
        String description = gcEvent.getDescription();
        int idx = description.indexOf("\n==========");
        if (idx > 0) {
            description = description.substring(0, idx);
        }
        ldEvent.setDescription(description);
    }

    private void updateEventWithLDData(com.google.api.services.calendar.model.Event gcEvent, Event ldEvent) {
        gcEvent.setSummary(ldEvent.getName());

        var eventDates = dateToEventDateTime(ldEvent.getFrom(), ldEvent.getTo());
        gcEvent.setStart(eventDates.getLeft());
        gcEvent.setEnd(eventDates.getRight());

        StringBuilder additionalDescription = new StringBuilder();

        if (ldEvent.getLoc() != null) {
            additionalDescription.append("Místo události: ").append(ldEvent.getLoc()).append('\n');
        }
        if (ldEvent.getWeb() != null) {
            additionalDescription.append("Webová stránka: ").append(ldEvent.getWeb()).append('\n');
        }
        if (ldEvent.getAmountOfPlayers() != null) {
            additionalDescription.append("Počet hráčů: ").append(ldEvent.getAmountOfPlayers()).append('\n');
        }
        if (ldEvent.getLabels() != null && ldEvent.getLabels().size() > 0) {
            additionalDescription.append("Štítky: ");
            boolean first = true;
            for(Label label : ldEvent.getLabels()) {
                if (!first) {
                    additionalDescription.append(", ");
                } else {
                    first = false;
                }
                additionalDescription.append(label.getName());
            }
            additionalDescription.append(ldEvent.getAmountOfPlayers()).append('\n');
        }

        if (additionalDescription.length() > 0) {
            // Enhanced description
            gcEvent.setDescription(ldEvent.getDescription() + "\n==========\n" + additionalDescription.toString());
        }
        else {
            // Just the basic description
            gcEvent.setDescription(ldEvent.getDescription());
        }
    }

    public Calendar createService() throws IOException, GeneralSecurityException {
        ClassLoader classLoader = getClass().getClassLoader();
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(classLoader.getResourceAsStream("larp-calendar-309507-3d5a5f3f4231.json"))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR_READONLY));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return service;
    }

    public SyncStats syncEventsFromGoogleCalendar() {
        try {
            Calendar service = createService();
            long nowMillis = System.currentTimeMillis();

            /**
             * Get events in Google calendar
             */
            DateTime now = new DateTime(nowMillis);
            com.google.api.services.calendar.model.Events eventsResponse = service.events().list(CALENDAR_ID)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<com.google.api.services.calendar.model.Event> gcItems = eventsResponse.getItems();

            /**
             * Get events in LD calendar (better a week back to cover everything)
             */
            FilterEvent filter = new FilterEvent();
            filter.setFrom(new Date(nowMillis - 7 * 24 * 60 * 60 * 1000));
            List<Event> ldItems = events.filtered(new IModel<FilterEvent>() {
                @Override
                public FilterEvent getObject() {
                    return filter;
                }
            });
            Map<String, Event> ldItemsMap = new HashMap<>();
            for (Event ldEvent : ldItems) {
                String gcalEventId = ldEvent.getGCalEventId();
                if (gcalEventId != null) {
                    ldItemsMap.put(gcalEventId, ldEvent);
                }
            }

            SyncStats stats = new SyncStats(gcItems.size(), ldItems.size());

            /**
             * Add items from Google Calendar we do not have
             */
            for (com.google.api.services.calendar.model.Event gcEvent : gcItems) {
                Event ldEvent = ldItemsMap.get(gcEvent.getId());

                if (ldEvent != null) {
                    /**
                     * Event found in LD
                     */
                    if (ldEvent.getGCalEventLastUpdated() != gcEvent.getUpdated().getValue()) {
                        /**
                         * Event changed - update in LD
                         */
                        updateEventWithGCalData(ldEvent, gcEvent);
                        events.saveOrUpdate(ldEvent);
                        stats.incUpdated();
                    }
                } else {
                    /**
                     * Event not found in LD - create in LD
                     */
                    ldEvent = new Event();
                    updateEventWithGCalData(ldEvent, gcEvent);
                    events.saveOrUpdate(ldEvent);
                    stats.incCreated();
                }
            }

            logger.info(stats.toString());

            return stats;
        } catch (Exception e) {
            logger.error("Sync from Google Calendar failed: " + e.toString(), e);
        }
        return null;
    }

    public void addEvent(Event event) {
        try {
            Calendar service = createService();

            com.google.api.services.calendar.model.Event gcEvent = new com.google.api.services.calendar.model.Event();

            updateEventWithLDData(gcEvent, event);

            com.google.api.services.calendar.model.Event insertedEvent = service.events().insert(CALENDAR_ID, gcEvent).execute();

            event.setGCalEventId(insertedEvent.getId());
            event.setGCalEventLastUpdated(insertedEvent.getUpdated().getValue());
        }
        catch (Exception e) {
            logger.error("Error when creating event: " + e.toString(), e);

            // Rethrow
            throw new RuntimeException(e);
        }
    }

    public void updateEvent(Event event) {
        if (event.getGCalEventId() == null) {
            // Event not yet in Google Calendar - add it instead
            addEvent(event);
            return;
        }

        try {
            Calendar service = createService();

            com.google.api.services.calendar.model.Event gcEvent = service.events().get(CALENDAR_ID, event.getGCalEventId()).execute();

            updateEventWithLDData(gcEvent, event);

            com.google.api.services.calendar.model.Event updatedEvent = service.events().update(CALENDAR_ID, event.getGCalEventId(), gcEvent).execute();

            event.setGCalEventLastUpdated(updatedEvent.getUpdated().getValue());
        }
        catch (Exception e) {
            logger.error("Error when updating event: " + e.toString(), e);

            // Rethrow
            throw new RuntimeException(e);
        }
    }
}
