package cz.larpovadatabaze.calendar.model;

import cz.larpovadatabaze.calendar.Location;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Uid;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Event for the database.
 */
@Entity(name = "event")
public class Event implements cz.larpovadatabaze.api.Entity {
    @Transient
    private SimpleDateFormat czechDate = new SimpleDateFormat("dd.MM.YYYY");

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "event_id_seq", name = "id_gen")
    private Integer id;
    private String name;
    private String description;
    private String loc;
    private String web;
    private boolean deleted;
    @Column(name = "amountofplayers")
    private Integer amountOfPlayers;
    @Temporal(value = TemporalType.DATE)
    private Date from;
    @Temporal(value = TemporalType.DATE)
    private Date to;
    // Specify the mapping for the Location.
    @Embedded
    private Location location;
    @ManyToMany
    @JoinTable(name = "event_has_labels", joinColumns = {
            @JoinColumn(name = "event_id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "label_id")
    })
    private List<Label> labels;

    @ManyToMany
    @JoinTable(name = "csld_game_has_event", joinColumns = {
            @JoinColumn(name = "event_id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "game_id")
    })
    private List<Game> games; // It is possible to be associated with multiple games for the festivals for example. Mainly to show the festival at the game page.

    // TODO: Take a look whether all these properties make sense.
    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name = "added_by",
            referencedColumnName = "`id`",
            insertable = true,
            updatable = true
    )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @Fetch(FetchMode.SELECT)
    private CsldUser addedBy;

    // Constructor without parameters must be there for ORM usage.
    protected Event() {
    }

    /**
     * Prepare instance of Event with id and some defaults for others. Use only in tests.
     *
     * @param id Id to be provided
     */
    public Event(int id) {
        this.id = id;
    }

    public Event(List<Label> labels) {
        this.labels = labels;
    }

    public Event(int id, List<Label> labels) {
        this.id = id;
        this.labels = labels;
    }

    public Event(int id, Location location) {
        this.id = id;
        this.location = location;
    }

    public Event(int id, Calendar from, Calendar to) {
        this.id = id;

        this.from = from.getTime();
        this.to = to.getTime();
    }

    public Event(int id, String name, Calendar from, Calendar to, Location location, List<Label> labels) {
        this.id = id;
        this.name = name;
        this.from = from.getTime();
        this.to = to.getTime();
        this.location = location;
        this.labels = labels;
    }

    public Event(int id, String name, Calendar from, Calendar to, Integer amountOfPlayers, String loc, String description, String web) {
        this.id = id;
        this.name = name;
        this.from = from.getTime();
        this.to = to.getTime();
        this.amountOfPlayers = amountOfPlayers;
        this.loc = loc;
        this.description = description;
        this.web = web;
    }

    public String getName() {
        return name;
    }

    public Calendar getFrom() {
        if (from != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            return cal;
        } else {
            return null;
        }
    }

    @Transient
    public String getFromCzech() {
        return czechDate.format(from);
    }

    public Calendar getTo() {
        if (to != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(to);

            return cal;
        } else {
            return null;
        }
    }

    @Transient
    public String getToCzech() {
        return czechDate.format(to);
    }

    public Location getLocation() {
        return location;
    }

    public Integer getId() {
        return id;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public String getDescription() {
        return description;
    }

    public String getLoc() {
        return loc;
    }

    public Integer getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public void sanitize() {
        this.description = Jsoup.clean(getDescription(), Whitelist.basic());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != event.getId()) return false;
        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (from != null ? !from.equals(event.from) : event.from != null) return false;
        if (to != null ? !to.equals(event.to) : event.to != null) return false;
        return !(location != null ? !location.equals(event.location) : event.location != null);

    }

    @Override
    public int hashCode() {
        int result = 31 * id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", loc='" + loc + '\'' +
                ", amountOfPlayers=" + amountOfPlayers +
                ", from=" + from +
                ", to=" + to +
                ", location=" + location +
                '}';
    }

    public static Event getEmptyEvent() {
        return new Event(new ArrayList<>());
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @Transient
    public IModel<?> getDate() {

        return Model.of(czechDate.format(getFrom().getTime()) + " - " + czechDate.format(getTo().getTime()));
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getWeb() {
        return web;
    }

    public CsldUser getAddedBy() {
        return addedBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setAddedBy(CsldUser addedBy) {
        this.addedBy = addedBy;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public VEvent asIcalEvent() {
        Calendar dateFrom = getFrom();
        dateFrom.add(Calendar.DATE, 1);
        Calendar dateTo = getTo();
        dateTo.add(Calendar.DATE, 2);
        VEvent result;

        result = new VEvent(new net.fortuna.ical4j.model.Date(dateFrom), new net.fortuna.ical4j.model.Date(dateTo), getName());
        result.getProperties().add(new Uid(String.valueOf(getId())));

        return result;
    }
}
