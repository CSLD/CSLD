package cz.larpovadatabaze.graphql.fetchers;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.services.Labels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetcherUtils {
    private static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat isoDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Parse date from a string
     *
     * @param date String date
     * @param path Value path to be used in case of parse error
     *
     * @return Parsed date
     */
    public static Date parseDate(String date, String path) {
        try {
            return new Date(isoDateFormatter.parse(date).getTime());
        } catch (ParseException e) {
            throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Date not parsable", path);
        }
    }

    /**
     * Parse date from a string
     *
     * @param dateTime String datetime
     * @param path Value path to be used in case of parse error
     *
     * @return Parsed date
     */
    public static Date parseDateTime(String dateTime, String path) {
        try {
            return new Date(isoDateTimeFormatter.parse(dateTime).getTime());
        } catch (ParseException e) {
            throw new GraphQLException(GraphQLException.ErrorCode.INVALID_VALUE, "Date not parsable", path);
        }
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

}

