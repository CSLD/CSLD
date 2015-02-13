package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Table(name = "csld_csld_user", schema = "public", catalog = "")
@Entity
public class CsldUser implements Serializable, Identifiable, IAutoCompletable, IEntityWithImage {
    private Integer id;

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_person_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String password;

    @Column(
            name = "password",
            nullable = false,
            insertable = true,
            updatable = true,
            length = 2147483647
    )
    @Basic
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Integer lastRating;

    @Column(
            name = "last_rating",
            nullable = true,
            insertable = true,
            updatable = true,
            length = 2147483647
    )
    @Basic
    public Integer getLastRating() {
        return lastRating;
    }

    public void setLastRating(Integer lastRating) {
        this.lastRating = lastRating;
    }

    private Short role;

    @Column(
            name = "role",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Basic
    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
    }

    private Boolean isAuthor;

    @Column(
            name = "is_author",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Basic
    public Boolean getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(Boolean isAuthor) {
        this.isAuthor = isAuthor;
    }

    private Person person;

    @Embedded
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private Integer amountOfComments;

    @Column(
            name = "amount_of_comments",
            nullable = false,
            insertable = false,
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
            insertable = false,
            updatable = false)
    @Basic
    public Integer getAmountOfPlayed() {
        return amountOfPlayed;
    }

    public void setAmountOfPlayed(Integer amountOfPlayed) {
        this.amountOfPlayed = amountOfPlayed;
    }

    private Integer amountOfCreated;

    @Column(
            name = "amount_of_created",
            nullable = false,
            insertable = false,
            updatable = false)
    @Basic
    public Integer getAmountOfCreated() {
        return amountOfCreated;
    }

    public void setAmountOfCreated(Integer amountOfCreated) {
        this.amountOfCreated = amountOfCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsldUser csldUser = (CsldUser) o;

        if (id != null ? !id.equals(csldUser.id) : csldUser.id != null) return false;
        if (image != null ? !image.equals(csldUser.image) : csldUser.image != null) return false;
        if (password != null ? !password.equals(csldUser.password) : csldUser.password != null) return false;
        if (person != null ? !person.equals(csldUser.person) : csldUser.person != null) return false;
        if (role != null ? !role.equals(csldUser.role) : csldUser.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        return result;
    }

    private Game bestGame;

    @ManyToOne
    @JoinColumn(
            name = "best_game_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    @Fetch(FetchMode.SELECT)
    public Game getBestGame(){
        return bestGame;
    }

    public void setBestGame(Game bestGame){
        this.bestGame = bestGame;
    }

    private List<Game> authorOf;

    @ManyToMany(mappedBy = "authors")
    public List<Game> getAuthorOf() {
        return authorOf;
    }

    public void setAuthorOf(List<Game> authorOf) {
        this.authorOf = authorOf;
    }

    private List<UserPlayedGame> playedGames;

    @OneToMany(mappedBy = "playerOfGame")
    public List<UserPlayedGame> getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(List<UserPlayedGame> playedGames) {
        this.playedGames = playedGames;
    }

    private List<Language> userHasLanguages;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToMany(
            fetch = FetchType.LAZY ,
            cascade = javax.persistence.CascadeType.ALL
    )
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(
            name="csld_user_has_languages", schema="public",
            joinColumns = {
                    @JoinColumn(name="id_user", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name="language", nullable = false)
            }
    )
    public List<Language> getUserHasLanguages() {
        return userHasLanguages;
    }

    public void setUserHasLanguages(List<Language> userHasLanguages) {
        this.userHasLanguages = userHasLanguages;
    }

    private List<Comment> commented;

    @OneToMany(mappedBy = "user")
    public List<Comment> getCommented() {
        return commented;
    }

    public void setCommented(List<Comment> commented) {
        this.commented = commented;
    }

    private String defaultLang;

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    @Basic
    @Column(
            name="default_lang"
    )
    public String getDefaultLang() {
        return defaultLang;
    }

    private Image image;

    @ManyToOne(cascade= javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name = "image",
            referencedColumnName = "id",
            insertable = true,
            updatable = true
    )
    @Cascade(CascadeType.SAVE_UPDATE)
    public Image getImage() {
        return image;
    }

    @Override
    @Transient
    public IPredefinedImage getDefaultImage() {
        return PredefinedImage.DEFAULT_AUTHOR_ICON;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        if(getPerson() == null) {
            return null;
        }
        if(getPerson().getEmail() == null) {
            return null;
        }
        return String.format("%s %s, %s", person.getNickname(), person.getName(),  person.getEmail());
    }

    public static CsldUser getEmptyUser() {
        CsldUser emptyUser = new CsldUser();
        emptyUser.setAuthorOf(new ArrayList<Game>());
        emptyUser.setCommented(new ArrayList<Comment>());
        emptyUser.setImage(null);
        emptyUser.setPerson(Person.getEmptyPerson());
        emptyUser.setPlayedGames(new ArrayList<UserPlayedGame>());
        emptyUser.setRole(CsldRoles.USER.getRole());
        emptyUser.setUserHasLanguages(new ArrayList<Language>());
        return emptyUser;
    }
}
