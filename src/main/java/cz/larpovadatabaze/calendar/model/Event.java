package cz.larpovadatabaze.calendar.model;

import cz.larpovadatabaze.calendar.Location;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Event for the database.
 */
@Entity(name = "csld_event")
public class Event implements cz.larpovadatabaze.api.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_event_id_seq", name="id_gen")
    private Integer id;
    private String name;
    @Temporal(value = TemporalType.DATE)
    private Calendar from;
    @Temporal(value = TemporalType.DATE)
    private Calendar to;
    // Specify the mapping for the Location.
    @Embedded
    private Location location;
    private String language;

    /**
     * Prepare instance of Event with id and some defaults for others. For use in tests.
     */
    public Event() {
    }

    public Event(Location location) {
        this.location = location;
    }

    public Event(Calendar from, Calendar to) {
        this.from = from;
        this.to = to;
    }

    public Event(String name, Calendar from, Calendar to, Location location, String language) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.location = location;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public Calendar getFrom() {
        return from;
    }

    public Calendar getTo() {
        return to;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!id.equals(event.id)) return false;
        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (from != null ? !from.equals(event.from) : event.from != null) return false;
        if (to != null ? !to.equals(event.to) : event.to != null) return false;
        return !(location != null ? !location.equals(event.location) : event.location != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
