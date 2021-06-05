package cz.larpovadatabaze.calendar.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.services.FileService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * Synchronizes events with Google Calendar
 */
@Component
public class GoogleCalendarEvents {
    private final static Logger logger = Logger.getLogger(GoogleCalendarEvents.class);

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String APPLICATION_NAME = "LARP Database";

    /**
     * We renew expiration when we are less that this ms from
     * the expiration.
     */
    private static final long EXPIRATION_SAFE_MARGIN_MS = 120000;

    /**
     * When we were this close to expiration, better run sync - we
     * might have missed something.
     */
    private static final long EXPIRATION_SYNC_MARGIN_MS = 5000;

    private final Events events;
    private final FileService fileService;
    private final Environment env;

    /**
     * Unix time in ms of next expiration of calendar subscription. 0 when not subscribed.
     */
    private long calendarSubscriptionExpirationMs;

    /**
     * Statistics of a sync
     */
    public static class SyncStats {
        private final int gCalEvents;
        private final int ldEvents;
        private int eventsCreated;
        private int eventsUpdated;
        private int eventsDeleted;

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

        public void incDeleted() {
            this.eventsDeleted++;
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

        public int getEventsDeleted() {
            return eventsDeleted;
        }

        @Override
        public String toString() {
            return "Google Calendar sync stats: created: " + eventsCreated + ", updated: " + eventsUpdated + ", " +
                    eventsDeleted + ", gCalEvents: " + gCalEvents + ", ldEvents: " + ldEvents;
        }
    }

    @Autowired
    public GoogleCalendarEvents(Events events, FileService fileService, Environment env) {
        this.events = events;
        this.fileService = fileService;
        this.env = env;
    }

    private String getCalendarId() {
        return env.getProperty("calendar.google");
    }

    private String getSubscriptionExpiration() {
        return env.getProperty("calendar.subscription_expiration");
    }

    private String getCalendarSyncUrl() {
        return env.getProperty("calendar.sync_url");
    }

    /**
     * Convert Google Calendar event date/time to Larp Database date
     *
     * @param eventDate Google Calendar event date
     * @param isEndDate Whether this is end date
     * @return Date for Larp Database
     */
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

    /**
     * @param cal Input calendar
     * @return Whether calendar is at midnight
     */
    private boolean isMidnight(java.util.Calendar cal) {
        return cal.get(GregorianCalendar.HOUR) == 0 && cal.get(GregorianCalendar.MINUTE) == 0 && cal.get(GregorianCalendar.SECOND) == 0 && cal.get(GregorianCalendar.MILLISECOND) == 0;
    }

    /**
     * Convert start / end dates to format needed for Google Calendar
     *
     * @param startCal Calendar for start of the event
     * @param endCal   Calendar for end of the event
     * @return Pair of start / end event time
     */
    private Pair<EventDateTime, EventDateTime> dateToEventDateTime(java.util.Calendar startCal, java.util.Calendar endCal) {
        EventDateTime gCalStart = new EventDateTime();
        EventDateTime gCalEnd = new EventDateTime();

        boolean wholeDay = isMidnight(startCal) && isMidnight(endCal);
        if (wholeDay) {
            // Event is whole day
            ZonedDateTime zdtStart = ZonedDateTime.of(startCal.get(GregorianCalendar.YEAR), startCal.get(GregorianCalendar.MONTH) + 1, startCal.get(GregorianCalendar.DATE), 0, 0, 0, 0, ZoneId.of("GMT"));
            gCalStart.setDate(new DateTime(true, zdtStart.toInstant().toEpochMilli(), null));

            // For whole day events we need to move end one day for Google Calendar
            ZonedDateTime zdtEnd = ZonedDateTime.of(endCal.get(GregorianCalendar.YEAR), endCal.get(GregorianCalendar.MONTH) + 1, endCal.get(GregorianCalendar.DATE), 0, 0, 0, 0, ZoneId.of("GMT"));
            zdtEnd = zdtEnd.plus(1, ChronoField.DAY_OF_YEAR.getBaseUnit());
            gCalEnd.setDate(new DateTime(true, zdtEnd.toInstant().toEpochMilli(), null));
        } else {
            // Event has set time of start / end
            gCalStart.setDateTime(new DateTime(startCal.getTime(), TimeZone.getTimeZone("Europe/Prague")));
            gCalEnd.setDateTime(new DateTime(endCal.getTime(), TimeZone.getTimeZone("Europe/Prague")));
        }

        return Pair.of(gCalStart, gCalEnd);
    }

    /**
     * Update Larp Database event with Google Calendar event data
     *
     * @param ldEvent Larp Database event - destination
     * @param gcEvent Google calendar event - source
     */
    private void updateEventWithGCalData(Event ldEvent, com.google.api.services.calendar.model.Event gcEvent) {
        ldEvent.setGCalEventId(gcEvent.getId());
        ldEvent.setGCalEventLastUpdated(gcEvent.getUpdated().getValue());
        ldEvent.setName(gcEvent.getSummary());
        ldEvent.setFrom(eventDateTimeToDate(gcEvent.getStart(), false));
        ldEvent.setTo(eventDateTimeToDate(gcEvent.getEnd(), true));
        ldEvent.setLoc(gcEvent.getLocation());

        // Strip of description at may "="s - in case we created that event
        String description = gcEvent.getDescription();
        int idx = description == null ? -1 : description.indexOf("\n==========");
        if (idx >= 0) {
            description = description.substring(0, idx);
        }
        ldEvent.setDescription(description);
    }

    /**
     * Update Google Calendar event with data from Larp Database
     *
     * @param gcEvent Google Calendar event - destination
     * @param ldEvent Larp Database event - source
     */
    private void updateEventWithLDData(com.google.api.services.calendar.model.Event gcEvent, Event ldEvent) {
        gcEvent.setSummary(ldEvent.getName());

        var eventDates = dateToEventDateTime(ldEvent.getFrom(), ldEvent.getTo());
        gcEvent.setStart(eventDates.getLeft());
        gcEvent.setEnd(eventDates.getRight());

        gcEvent.setLocation(ldEvent.getLoc());

        StringBuilder additionalDescription = new StringBuilder();

        if (ldEvent.getWeb() != null && ldEvent.getWeb().length() > 0) {
            additionalDescription.append("Webová stránka: ").append(ldEvent.getWeb()).append('\n');
        }
        if (ldEvent.getAmountOfPlayers() != null) {
            additionalDescription.append("Počet hráčů: ").append(ldEvent.getAmountOfPlayers()).append('\n');
        }
        if (ldEvent.getLabels() != null && ldEvent.getLabels().size() > 0) {
            additionalDescription.append("Štítky: ");
            boolean first = true;
            for (Label label : ldEvent.getLabels()) {
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
        } else {
            // Just the basic description
            gcEvent.setDescription(ldEvent.getDescription());
        }
    }

    /**
     * Create calendar service for communication with Google Calendar
     */
    private Calendar createService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(fileService.getFileAsStream("larp-calendar-config.json"))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return service;
    }

    /**
     * Check whether Google Calendar subscription is about to expire. If so, renew it. When
     * it already expired, run sync.
     */
    public synchronized void checkCalendarSubscription() {
        long oldExpiration = calendarSubscriptionExpirationMs;

        System.out.println("Environment=" + env.toString());
        System.out.println("calendar_id=" + getCalendarId());
        System.out.println("sync_url=" + getCalendarSyncUrl());
        System.out.println("expiration=" + getSubscriptionExpiration());

        long now = new Date().getTime();
        if (now + EXPIRATION_SAFE_MARGIN_MS > oldExpiration) {
            // Renew subscription
            try {
                Channel channel = new Channel();
                channel.setId(RandomStringUtils.randomAlphanumeric(32));
                channel.setType("web_hook");
                channel.setAddress(getCalendarSyncUrl());
                channel.setParams(Map.of("ttl", getSubscriptionExpiration()));
                var res = createService().events().watch(getCalendarId(), channel).execute();

                calendarSubscriptionExpirationMs = res.getExpiration();

                logger.info("Subscription to Google Calendar successful, expires at " + calendarSubscriptionExpirationMs);
            } catch(Exception e) {
                logger.error("Subscription to Google Calendar failed: " + e.toString(), e);
            }
        }
    }

    /**
     * Sync events from Google Calendar to Larp Database
     */
    public synchronized SyncStats syncEventsFromGoogleCalendar() {
        try {
            Calendar service = createService();
            long nowMillis = System.currentTimeMillis();
            long limitMillis = nowMillis - 7 * 24 * 60 * 60 * 1000;

            /**
             * Get events in Google calendar
             */
            DateTime now = new DateTime(limitMillis);
            com.google.api.services.calendar.model.Events eventsResponse = service.events().list(getCalendarId())
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<com.google.api.services.calendar.model.Event> gcItems = eventsResponse.getItems();

//            for(com.google.api.services.calendar.model.Event event: gcItems) {
//                System.out.println("ID:" + event.getId() + ", name: " + event.getSummary());
//            }

            /**
             * Get events in LD calendar (better a week back to cover everything)
             */
            FilterEvent filter = new FilterEvent();
            filter.setFrom(new Date(limitMillis));
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

                    // Remove from map - serves as mark we have seen that event
                    ldItemsMap.remove(gcEvent.getId());

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

            /**
             * Check events we have not got from server
             */
            for(Event ldEvent : ldItemsMap.values()) {
                if (ldEvent.getTo().getTimeInMillis() > nowMillis) {
                    // Event is still in progress => we should have it produced in Google Calendar query but
                    // it was not here => it was deleted => mark it deleted in Larp Database
                    ldEvent.setDeleted(true);
                    events.saveOrUpdate(ldEvent);
                    stats.incDeleted();
                }
            }

            logger.info(stats.toString());

            return stats;
        } catch (Exception e) {
            logger.error("Sync from Google Calendar failed: " + e.toString(), e);
        }
        return null;
    }

    /**
     * Create event in Google Calendar based on LD event
     *
     * @param event Larp Database event
     */
    public synchronized void createEvent(Event event) {
        try {
            Calendar service = createService();

            com.google.api.services.calendar.model.Event gcEvent = new com.google.api.services.calendar.model.Event();

            updateEventWithLDData(gcEvent, event);

            com.google.api.services.calendar.model.Event insertedEvent = service.events().insert(getCalendarId(), gcEvent).execute();

            event.setGCalEventId(insertedEvent.getId());
            event.setGCalEventLastUpdated(insertedEvent.getUpdated().getValue());
        } catch (Exception e) {
            logger.error("Error when creating event '" + event.getName() + "': " + e.toString(), e);

            // Rethrow
            throw new RuntimeException(e);
        }
    }

    /**
     * Update event in Google Calendar based on LD event.
     *
     * @param event Larp Database event. When it does not have associated Google Calendar event,
     *              event in Google Calendar is created instead.
     */
    public synchronized void updateEvent(Event event) {
        if (event.getGCalEventId() == null) {
            // Event not yet in Google Calendar - add it instead
            createEvent(event);
            return;
        }

        try {
            Calendar service = createService();

            com.google.api.services.calendar.model.Event gcEvent = service.events().get(getCalendarId(), event.getGCalEventId()).execute();

            updateEventWithLDData(gcEvent, event);

            com.google.api.services.calendar.model.Event updatedEvent = service.events().update(getCalendarId(), event.getGCalEventId(), gcEvent).execute();

            event.setGCalEventLastUpdated(updatedEvent.getUpdated().getValue());
        } catch (Exception e) {
            logger.error("Error when updating event '" + event.getName() + "': " + e.toString(), e);

            // Rethrow
            throw new RuntimeException(e);
        }
    }

    /**
     * Deleted corresponding event in Google Calendar
     *
     * @param event Larp Database event
     */
    public synchronized void deleteEvent(Event event) {
        if (event.getGCalEventId() == null) {
            // No associated Google Calendar event
            return;
        }

        try {
            Calendar service = createService();

            service.events().delete(getCalendarId(), event.getGCalEventId()).execute();
        } catch (Exception e) {
            logger.error("Error when deleting event '" + event.getName() + "': " + e.toString(), e);

            // Rethrow
            throw new RuntimeException(e);
        }
    }
}
