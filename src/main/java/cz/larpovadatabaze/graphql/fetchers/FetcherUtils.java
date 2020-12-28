package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;

import java.time.DateTimeException;
import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetcherUtils {
//    private static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//    private static SimpleDateFormat isoDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static ZoneId apiTimeZone = ZoneId.of("Europe/Prague");

    /**
     * Parse date from a string expecting it to be in Europe/Prague TZ
     *
     * @param date String date
     * @param path Value path to be used in case of parse error
     *
     * @return Parsed date
     */
    public static Date parseDate(String date, String path) {
        try {
            return new Date(ZonedDateTime.parse(date + "T00:00:00Z").withZoneSameLocal(apiTimeZone).toInstant().toEpochMilli());
        } catch (DateTimeException e) {
            throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Date not parsable", path);
        }
    }

    /**
     * Parse date and time from a string expecting it to be in Europe/Prague TZ
     *
     * @param dateTime String datetime
     * @param path Value path to be used in case of parse error
     *
     * @return Parsed date
     */
    public static Date parseDateTime(String dateTime, String path) {
        try {
            return new Date(ZonedDateTime.parse(dateTime + "Z").withZoneSameLocal(apiTimeZone).toInstant().toEpochMilli());
        } catch (DateTimeException e) {
            throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Date not parsable", path);
        }
    }

    /**
     * @param calendar Calendar to format
     *
     * @return ISO datetime in Europe/Prague zone
     */
    public static String formatDateTime(GregorianCalendar calendar) {
        return calendar.toZonedDateTime().withZoneSameInstant(apiTimeZone).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * @param calendar Calendar to format
     *
     * @return ISO date in Europe/Prague zone
     */
    public static String formatDate(GregorianCalendar calendar) {
        return calendar.toZonedDateTime().withZoneSameInstant(apiTimeZone).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Find label in the list (by name)
     *
     * @param labelList Label list
     * @param name      Name to find
     * @return Label or null
     */
    private static Label findLabel(List<Label> labelList, String name) {
        for (Label label : labelList) {
            if (label.getName().equalsIgnoreCase(name)) {
                return label;
            }
        }

        return null;
    }

    /**
     * Get label object by ids & new label specifications
     *
     * @param labels Labels CRUD
     * @param labelIds  List of label ids
     * @param newLabels List of label specifications. Tries to find label by name and when it does not exist, creates it.
     * @parqm loggedInUser Currently logged in (to know who is adding the labels)
     *
     * @return Label objects
     */
    public static List<Label> getLabels(Labels labels, List<String> labelIds, List<Map<String, Object>> newLabels, CsldUser loggedInUser) {
        List<Label> res = new ArrayList<>();

        // Fetch existing labels
        for (String id : labelIds) {
            Label label = labels.getById(Integer.parseInt(id));
            if (label == null) {
                throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Label with id " + id + " does not exist", "input.labels");
            }
            res.add(label);
        }

        // Create new labels (if necessary)
        List<Label> allLabels = labels.getAll();
        List<Label> createdLabels = new ArrayList<>();
        for (Map<String, Object> newLabel : newLabels) {
            String name = (String) newLabel.get("name");
            String description = (String) newLabel.get("description");

            // Try to find existing label by name
            Label label = findLabel(allLabels, name);
            if (label == null) {
                label = findLabel(createdLabels, name);
                if (label == null) {
                    // We need to create new label
                    label = new Label();
                    label.setName(name);
                    label.setDescription(description);
                    label.setAddedBy(loggedInUser);

                    labels.saveOrUpdate(label);

                    createdLabels.add(label);
                }
            }
            res.add(label);
        }

        return res;
    }

    public static List<Label> getLabels(Labels labels, List<String> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }

        return ids.stream().map(id -> labels.getById(Integer.parseInt(id))).collect(Collectors.toList());
    }
}

