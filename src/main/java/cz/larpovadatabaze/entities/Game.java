package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.lang.*;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.*;

import cz.larpovadatabaze.api.Identifiable;


/**
 * Name of every game must be unique. Or at least I expect it to be.
 */
@Entity
@Table(schema = "public", name="csld_game")
public class Game implements Serializable, Identifiable, IAutoCompletable, IEntityWithImage, TranslatableEntity {
    public Game(){ }

    private Integer id;

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_game_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    private GameHasLanguages defaultLanguage = new GameHasLanguages();

    @Transient
    private String name;

    @Transient
    public String getName() {
        if(name == null) {
            new TranslatableEntityTranslator(new DbSessionLanguageSolver()).translate(this);
        }
        return name;
    }

    @Transient
    public void setName(String name) {
        this.name = name;
        defaultLanguage.setName(name);
    }

    @Transient
    private String description;

    @Transient
    public String getDescription() {
        if(description == null) {
            new TranslatableEntityTranslator(new DbSessionLanguageSolver()).translate(this);
        }
        return description;
    }

    @Transient
    public void setDescription(String description) {
        this.description = description;
        defaultLanguage.setDescription(description);
    }
    @Transient
    private String lang;

    @Transient
    public String getLang() {
        if(lang == null) {
            new TranslatableEntityTranslator(new DbSessionLanguageSolver()).translate(this);
        }
        return lang;
    }

    @Transient
    public void setLang(String lang) {
        this.lang = lang;
        if(availableLanguages  == null) {
            availableLanguages = new ArrayList<GameHasLanguages>();
        }
        LocaleProvider provider = new CodeLocaleProvider();
        Locale actualLanguage = provider.transformToLocale(lang);
        for(GameHasLanguages language: availableLanguages) {
            // Ignore already added language.
            if(language.getLanguage().getLanguage().equals(actualLanguage)){
                return;
            }
        }

        defaultLanguage.setGame(this);
        defaultLanguage.setLanguage(new Language(lang));
        availableLanguages.add(defaultLanguage);
    }

    private Integer year;

    @Column(
            name = "year",
            nullable = true,
            insertable = true,
            updatable = true
    )
    @Basic
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    private String web;

    @Column(
            name = "web",
            nullable = true,
            insertable = true,
            updatable = true,
            length = 2147483647
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
            name = "hours",
            nullable = true,
            insertable = true,
            updatable = true
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
            name = "days",
            nullable = true,
            insertable = true,
            updatable = true
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
            name = "players",
            nullable = true,
            insertable = true,
            updatable = true
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
            name = "men_role",
            nullable = true,
            insertable = true,
            updatable = true
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
            name = "women_role",
            nullable = true,
            insertable = true,
            updatable = true
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
            name = "both_role",
            nullable = true,
            insertable = true,
            updatable = true
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
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Basic
    public Double getTotalRating(){
        return totalRating;
    }

    public void setTotalRating(Double totalRating){
        this.totalRating = totalRating;
    }

    private Double averageRating;

    @Column(
            name = "average_rating",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Basic
    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    private Timestamp added;

    @Column(
            name = "added",
            nullable = false,
            insertable = true,
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
            insertable = true,
            updatable = false)
    @Basic
    public Integer getAmountOfComments() {
        return amountOfComments;
    }

    public void setAmountOfComments(Integer amountOfComments) {
        this.amountOfComments = amountOfComments;
    }

    private Integer amountOfPlayed;

    @Column(
            name = "amount_of_played",
            nullable = false,
            insertable = true,
            updatable = false)
    @Basic
    public Integer getAmountOfPlayed() {
        return amountOfPlayed;
    }

    public void setAmountOfPlayed(Integer amountOfPlayed) {
        this.amountOfPlayed = amountOfPlayed;
    }

    private Integer amountOfRatings;

    @Column(
            name = "amount_of_ratings",
            nullable = true,
            insertable = true,
            updatable = false)
    @Basic
    public Integer getAmountOfRatings() {
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

    @Column(name="ratingsDisabled")
    @Basic
    public boolean isRatingsDisabled() {
        return ratingsDisabled;
    }

    public void setRatingsDisabled(boolean ratingsDisabled) {
        this.ratingsDisabled = ratingsDisabled;
    }

    private boolean commentsDisabled;

    @Column(name="commentsDisabled")
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

    private List<CsldUser> authors;

    @JoinTable(
            name = "csld_game_has_author",
            schema = "public",
            joinColumns = @JoinColumn(
                    name = "id_game",
                    referencedColumnName = "id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_user",
                    referencedColumnName = "id",
                    nullable = false)
    )
    @ManyToMany
    public List<CsldUser> getAuthors() {
        return authors;
    }

    public void setAuthors(List<CsldUser> authors) {
        this.authors = authors;
    }

    private List<CsldGroup> groupAuthor;

    @JoinTable(
            name = "csld_game_has_group",
            schema = "public",
            joinColumns = @JoinColumn(
                    name = "id_game",
                    referencedColumnName = "id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_group",
                    referencedColumnName = "id",
                    nullable = false)
    )
    @ManyToMany
    public List<CsldGroup> getGroupAuthor() {
        return groupAuthor;
    }

    public void setGroupAuthor(List<CsldGroup> groupAuthor) {
        this.groupAuthor = groupAuthor;
    }

    private List<Photo> photos;

    @OneToMany(mappedBy = "game")
    @Cascade(CascadeType.ALL)
    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    private Image image;

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name = "image",
            referencedColumnName = "id",
            insertable = true,
            updatable = true
    )
    public Image getImage() {
        return image;
    }

    @Override
    @Transient
    public IPredefinedImage getDefaultImage() {
        return PredefinedImage.DEFAULT_GAME_ICON;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private List<Label> labels;

    @JoinTable(
            name = "csld_game_has_label",
            schema = "public",
            joinColumns = @JoinColumn(
                    name = "id_game",
                    referencedColumnName = "id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_label",
                    referencedColumnName = "id",
                    nullable = false)
    )
    @ManyToMany(cascade = {javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REMOVE })
    @Cascade(value = { CascadeType.SAVE_UPDATE, CascadeType.DELETE})
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
            referencedColumnName = "id",
            insertable = true,
            updatable = true
    )
    @Cascade(CascadeType.SAVE_UPDATE)
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
            referencedColumnName = "id",
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

    private List<Rating> ratings;

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

    private List<Comment> comments;

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

    private List<UserPlayedGame> played;

    @OneToMany(mappedBy = "playedBy")
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    public List<UserPlayedGame> getPlayed() {
        return played;
    }

    public void setPlayed(List<UserPlayedGame> played) {
        this.played = played;
    }

    private List<GameHasLanguages> availableLanguages;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    public List<GameHasLanguages> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(List<GameHasLanguages> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    public Photo coverPhoto;

    @ManyToOne
    @JoinColumn(
            name = "cover_photo",
            referencedColumnName = "id",
            insertable = true,
            updatable = true
    )
    public Photo getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(Photo coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    @Transient
    public List<TranslationEntity> getLanguages(){
        if(availableLanguages == null) {
            return null;
        }
        return new ArrayList<TranslationEntity>(availableLanguages);
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getName();
    }

    public static Game getEmptyGame() {
        Game emptyGame = new Game();
        emptyGame.setGroupAuthor(new ArrayList<CsldGroup>());
        emptyGame.setAuthors(new ArrayList<CsldUser>());
        emptyGame.setComments(new ArrayList<Comment>());
        emptyGame.setLabels(new ArrayList<Label>());
        emptyGame.setImage(null);
        emptyGame.setPhotos(new ArrayList<Photo>());
        emptyGame.setPlayed(new ArrayList<UserPlayedGame>());
        emptyGame.setRatings(new ArrayList<Rating>());
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
}










