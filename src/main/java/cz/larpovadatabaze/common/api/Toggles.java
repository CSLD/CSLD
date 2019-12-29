package cz.larpovadatabaze.common.api;

public class Toggles {
    private Toggles() {
        throw new IllegalStateException("Utility class");
    }

    // Decides whether calendar should be visible to users as well as editors.
    public static final String CALENDAR = "csld.calendar";
}
