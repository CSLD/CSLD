package cz.larpovadatabaze.common.entities;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.common.Identifiable;
import cz.larpovadatabaze.common.components.multiac.IAutoCompletable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Name of every game must be unique. Or at least I expect it to be.
 */
@Entity
@Table(name = "csld_game")
public class Game implements Serializable, Identifiable<Integer>, IAutoCompletable, IEntityWithImage {
    public Game() {
    }

    public Game(String name, String description, CsldUser addedBy, List<CsldUser> authors,
                List<Label> labels, Timestamp added) {
        this(name, description, addedBy, authors, labels, added, null);
    }

    public Game(Integer id, String name, String description, CsldUser addedBy, List<CsldUser> authors,
                List<Label> labels, Timestamp added) {
        this(id, name, description, addedBy, authors, labels, added, null, 0);
    }

    public Game(String name, String description, CsldUser addedBy, List<CsldUser> authors,
                List<Label> labels, Timestamp added, Image coverImage) {
        this(name, description, addedBy, authors, labels, added, coverImage, 0);
    }

    public Game(Integer id, String name, String description, CsldUser addedBy, List<CsldUser> authors,
                List<Label> labels, Timestamp added, Image coverImage, Integer year) {
        this(name, description, addedBy, authors, labels, added, coverImage, year);

        this.id = id;
    }

    public Game(String name, String description, CsldUser addedBy, List<CsldUser> authors,
                List<Label> labels, Timestamp added, Image coverImage, Integer year) {
        this.totalRating = 0d;
        this.name = name;
        this.description = description;
        this.addedBy = addedBy;
        this.added = added;
        this.amountOfComments = 0;
        this.amountOfRatings = 0;
        this.amountOfPlayed = 0;
        this.authors = authors;
        this.averageRating = 0.0;
        this.menRole = 4;
        this.womenRole = 5;
        this.labels = labels;
        this.coverImage = coverImage;
        this.year = year;

        for (CsldUser author : authors) {
            author.getAuthorOf().add(this);
        }
    }

    private Integer id;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_game")
    @SequenceGenerator(sequenceName = "csld_game_id_seq", name = "id_gen_game", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String name;

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    @Column(name="description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Integer year;

    @Column(
            name = "year"
    )
    @Basic
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    private String blueprintPath;

    @Column(
            name = "blueprint_path"
    )
    @Basic
    public String getBlueprintPath() {
        return blueprintPath;
    }

    public void setBlueprintPath(String blueprintPath) {
        this.blueprintPath = blueprintPath;
    }

    private String web;

    @Column(
            name = "web",
            length = 20000
    )
    @Basic
    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    private Integer hours;

    @Column(
            name = "hours"
    )
    @Basic
    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    private Integer days;

    @Column(
            name = "days"
    )
    @Basic
    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    private Integer players;

    @Column(
            name = "players"
    )
    @Basic
    public Integer getPlayers() {
        return players;
    }

    public void setPlayers(Integer players) {
        this.players = players;
    }

    private Integer menRole;

    @Column(
            name = "men_role"
    )
    @Basic
    public Integer getMenRole() {
        return menRole;
    }

    public void setMenRole(Integer menRole) {
        this.menRole = menRole;
    }

    private Integer womenRole;

    @Column(
            name = "women_role"
    )
    @Basic
    public Integer getWomenRole() {
        return womenRole;
    }

    public void setWomenRole(Integer womenRole) {
        this.womenRole = womenRole;
    }

    private Integer bothRole;

    @Column(
            name = "both_role"
    )
    @Basic
    public Integer getBothRole() {
        return bothRole;
    }

    public void setBothRole(Integer bothRole) {
        this.bothRole = bothRole;
    }

    private Double totalRating;

    @Column(
            name = "total_rating",
            nullable = false
    )
    @Basic
    public Double getTotalRating(){
        if (totalRating == null) {
            return 0.0;
        }
        return totalRating;
    }

    public void setTotalRating(Double totalRating){
        this.totalRating = totalRating;
    }

    private Double averageRating;

    @Column(
            name = "average_rating",
            nullable = false
    )
    @Basic
    public Double getAverageRating() {
        if (averageRating == null) {
            return 0.0;
        }
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    private Timestamp added;

    @Column(
            name = "added",
            nullable = false,
            updatable = false)
    @Basic
    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }

    private Integer amountOfComments;

    @Column(
            name = "amount_of_comments",
            nullable = false,
            updatable = false)
    @Basic
    public Integer getAmountOfComments() {
        if (amountOfComments == null) {
            return 0;
        }
        return amountOfComments;
    }

    public void setAmountOfComments(Integer amountOfComments) {
        this.amountOfComments = amountOfComments;
    }

    private Integer amountOfPlayed;

    @Column(
            name = "amount_of_played",
            nullable = false,
            updatable = false)
    @Basic
    public Integer getAmountOfPlayed() {
        if (amountOfPlayed == null) {
            return 0;
        }
        return amountOfPlayed;
    }

    public void setAmountOfPlayed(Integer amountOfPlayed) {
        this.amountOfPlayed = amountOfPlayed;
    }

    private Integer amountOfRatings;

    @Column(
            name = "amount_of_ratings",
            updatable = false)
    @Basic
    public Integer getAmountOfRatings() {
        if (amountOfRatings == null) {
            return 0;
        }
        return amountOfRatings;
    }

    public void setAmountOfRatings(Integer amountOfRatings) {
        this.amountOfRatings = amountOfRatings;
    }

    private String galleryURL;


    @Column(name="gallery_url")
    @Basic
    public String getGalleryURL() {
        return galleryURL;
    }

    public void setGalleryURL(String galleryURL) {
        this.galleryURL = galleryURL;
    }

    private String photoAuthor;

    @Column(name="photo_author")
    @Basic
    public String getPhotoAuthor() {
        return photoAuthor;
    }

    public void setPhotoAuthor(String photoAuthor) {
        this.photoAuthor = photoAuthor;
    }

    private boolean deleted;

    @Column(name="deleted")
    @Basic
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    private boolean ratingsDisabled;

    @Column(name="ratingsdisabled")
    @Basic
    public boolean isRatingsDisabled() {
        return ratingsDisabled;
    }

    public void setRatingsDisabled(boolean ratingsDisabled) {
        this.ratingsDisabled = ratingsDisabled;
    }

    private boolean commentsDisabled;

    @Column(name="commentsdisabled")
    @Basic
    public boolean isCommentsDisabled() {
        return commentsDisabled;
    }

    public void setCommentsDisabled(boolean commentsDisabled) {
        this.commentsDisabled = commentsDisabled;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (added != null ? !added.equals(game.added) : game.added != null) return false;
        if (bothRole != null ? !bothRole.equals(game.bothRole) : game.bothRole != null) return false;
        if (days != null ? !days.equals(game.days) : game.days != null) return false;
        if (description != null ? !description.equals(game.description) : game.description != null) return false;
        if (hours != null ? !hours.equals(game.hours) : game.hours != null) return false;
        if (id != null ? !id.equals(game.id) : game.id != null) return false;
        if (menRole != null ? !menRole.equals(game.menRole) : game.menRole != null) return false;
        if (name != null ? !name.equals(game.name) : game.name != null) return false;
        if (players != null ? !players.equals(game.players) : game.players != null) return false;
        if (web != null ? !web.equals(game.web) : game.web != null) return false;
        if (womenRole != null ? !womenRole.equals(game.womenRole) : game.womenRole != null) return false;
        if (year != null ? !year.equals(game.year) : game.year != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (web != null ? web.hashCode() : 0);
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (days != null ? days.hashCode() : 0);
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + (menRole != null ? menRole.hashCode() : 0);
        result = 31 * result + (womenRole != null ? womenRole.hashCode() : 0);
        result = 31 * result + (bothRole != null ? bothRole.hashCode() : 0);
        result = 31 * result + (added != null ? added.hashCode() : 0);
        return result;
    }

    private List<CsldUser> authors = new ArrayList<>();

    @JoinTable(
            name = "csld_game_has_author",
            joinColumns = @JoinColumn(
                    name = "id_game",
                    referencedColumnName = "`id`",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_user",
                    referencedColumnName = "`id`",
                    nullable = false)
    )
    @ManyToMany
    public List<CsldUser> getAuthors() {
        return authors;
    }

    public void setAuthors(List<CsldUser> authors) {
        this.authors = authors;
    }

    private List<CsldGroup> groupAuthor = new ArrayList<>();

    @JoinTable(
            name = "csld_game_has_group",
            joinColumns = @JoinColumn(
                    name = "id_game",
                    referencedColumnName = "`id`",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_group",
                    referencedColumnName = "`id`",
                    nullable = false)
    )
    @ManyToMany
    public List<CsldGroup> getGroupAuthor() {
        return groupAuthor;
    }

    public void setGroupAuthor(List<CsldGroup> groupAuthor) {
        this.groupAuthor = groupAuthor;
    }

    private List<Event> events = new ArrayList<>();

    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    @Transient
    public IPredefinedImage getDefaultImage() {
        return PredefinedImage.DEFAULT_GAME_ICON;
    }

    private List<Label> labels = new ArrayList<>();

    @JoinTable(
            name = "csld_game_has_label",
            joinColumns = @JoinColumn(
                    name = "id_game",
                    referencedColumnName = "`id`",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_label",
                    referencedColumnName = "`id`",
                    nullable = false)
    )
    @ManyToMany(cascade = {javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REMOVE })
    @Cascade(value = { org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    private Video video;

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name = "video",
            referencedColumnName = "`id`",
            insertable = true,
            updatable = true
    )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @Fetch(FetchMode.SELECT)
    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    private CsldUser addedBy;

    @ManyToOne
    @JoinColumn(
            name = "added_by",
            referencedColumnName = "`id`",
            insertable = true,
            updatable = false
    )
    @Fetch(FetchMode.SELECT)
    public CsldUser getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(CsldUser addedBy) {
        this.addedBy = addedBy;
    }

    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(
            mappedBy = "game"
    )
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    private List<Comment> comments = new ArrayList<>();

    @OneToMany(
            mappedBy = "game"
    )
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    private Image coverImage;

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
        name = "cover_image",
        referencedColumnName = "`id`",
        insertable = true,
        updatable = true
    )
    public Image getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Image coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    @Transient
    public Image getImage() {
        return coverImage;
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getName();
    }

    public static Game getEmptyGame() {
        Game emptyGame = new Game();
        emptyGame.setGroupAuthor(new ArrayList<>());
        emptyGame.setAuthors(new ArrayList<>());
        emptyGame.setComments(new ArrayList<>());
        emptyGame.setLabels(new ArrayList<>());
        emptyGame.setCoverImage(null);
        emptyGame.setPhotos(new ArrayList<>());
        emptyGame.setRatings(new ArrayList<>());
        emptyGame.setAmountOfComments(0);
        emptyGame.setAmountOfPlayed(0);
        emptyGame.setAmountOfRatings(0);
        emptyGame.setAverageRating(0.0);
        emptyGame.setTotalRating(0.0);
        return emptyGame;
    }

    @Transient
    private int first = 0;

    @Transient
    public int getFirst(){
        return first;
    }

    @Transient
    public void setFirst(int first) {
        this.first = first;
    }

    @ManyToMany
    @JoinTable(name="csld_game_has_event", joinColumns = {
            @JoinColumn(name="game_id")
    }, inverseJoinColumns = {
            @JoinColumn(name="event_id")
    })
    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}










