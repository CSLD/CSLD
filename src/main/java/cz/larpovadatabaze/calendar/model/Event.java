package cz.larpovadatabaze.calendar.model;

import cz.larpovadatabaze.calendar.Location;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

/**
 * Event for the database.
 */
@Entity(name = "event")
public class Event implements cz.larpovadatabaze.api.Entity {
    @Id
    private String id;
    private String name;
    private String date;
    private String description;
    private String loc;
    @Temporal(value = TemporalType.DATE)
    private Calendar from;
    @Temporal(value = TemporalType.DATE)
    private Calendar to;
    // Specify the mapping for the Location.
    @Embedded
    private Location location;
    private String language;
    @ManyToMany
    @JoinTable(name = "event_has_labels")
    private Collection<Label> labels;

    // Constructor without parameters must be there for ORM usage.
    protected Event() {
    }

    /**
     * Prepare instance of Event with id and some defaults for others. Use only in tests.
     *
     * @param id Id to be provided
     */
    public Event(String id) {
        this.id = id;
    }

    public Event(String id, Collection<Label> labels) {
        this.id = id;
        this.labels = labels;
    }

    public Event(String id, Location location) {
        this.id = id;
        this.location = location;
    }

    public Event(String id, Calendar from, Calendar to) {
        this.id = id;
        this.from = from;
        this.to = to;
    }

    public Event(String id, String name, Calendar from, Calendar to, Location location, String language, Collection<Label> labels) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.location = location;
        this.language = language;
        this.labels = labels;
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

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public Collection<Label> getLabels() {
        return labels;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getLoc() {
        return loc;
    }

    public void sanitize() {
        this.description = Jsoup.clean(getDescription(), Whitelist.basic());
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
        int result = (id != null ? id.hashCode(): 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    public static Event getEmptyEvent() {
        return new Event(UUID.randomUUID().toString());
    }
}
