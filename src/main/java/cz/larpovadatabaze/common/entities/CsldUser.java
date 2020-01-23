package cz.larpovadatabaze.common.entities;

import cz.larpovadatabaze.common.Identifiable;
import cz.larpovadatabaze.common.components.multiac.IAutoCompletable;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.Pwd;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
@Table(name = "csld_csld_user")
@Entity
public class CsldUser implements Serializable, Identifiable<Integer>, IAutoCompletable, IEntityWithImage {
    public CsldUser() {
    }

    public CsldUser(Integer id, String email, String name, String nickName, String city, String description, Short role,
                    String password) {
        this(email, name, nickName, city, description, role, password);

        this.id = id;
    }

    public CsldUser(String email, String name, String nickName, String city, String description, Short role,
                    String password) {
        Person person = new Person(
                name,
                description,
                email,
                nickName,
                new Date(Calendar.getInstance().getTime().getTime()),
                city);

        this.person = person;
        this.password = Pwd.generateStrongPasswordHash(password, person.getEmail());
        this.amountOfComments = 1;
        this.amountOfCreated = 1;
        this.amountOfPlayed = 1;
        this.isAuthor = false;
        this.role = role;
        this.defaultLang = "cs";
    }

    private Integer id;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_user")
    @SequenceGenerator(sequenceName = "csld_person_id_seq", name = "id_gen_user", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String password;

    @Column(name = "password")
    @Basic
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Integer lastRating;

    @Column(name = "last_rating")
    public Integer getLastRating() {
        return lastRating;
    }

    public void setLastRating(Integer lastRating) {
        this.lastRating = lastRating;
    }

    private Short role;

    @Column(name = "role")
    @Basic
    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
    }

    private Boolean isAuthor;

    @Column(name = "is_author")
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

    @Column(name = "amount_of_comments")
    public Integer getAmountOfComments() {
        return amountOfComments;
    }

    public void setAmountOfComments(Integer amountOfComments) {
        this.amountOfComments = amountOfComments;
    }

    private Integer amountOfPlayed;

    @Column(name = "amount_of_played")
    public Integer getAmountOfPlayed() {
        return amountOfPlayed;
    }

    public void setAmountOfPlayed(Integer amountOfPlayed) {
        this.amountOfPlayed = amountOfPlayed;
    }

    private Integer amountOfCreated;

    @Column(name = "amount_of_created")
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

    private List<Game> authorOf = new ArrayList<>();

    @ManyToMany(mappedBy = "authors")
    public List<Game> getAuthorOf() {
        return authorOf;
    }

    public void setAuthorOf(List<Game> authorOf) {
        this.authorOf = authorOf;
    }

    private List<UserPlayedGame> playedGames = new ArrayList<>();

    @OneToMany(mappedBy = "playerOfGame")
    public List<UserPlayedGame> getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(List<UserPlayedGame> playedGames) {
        this.playedGames = playedGames;
    }

    private List<Comment> commented = new ArrayList<>();

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
            referencedColumnName = "`id`"
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

        if (person.getNickname() == null) {
            return person.getName();
        }
        else {
            return String.format("%s %s", person.getNickname(), person.getName());
        }
    }

    public static CsldUser getEmptyUser() {
        CsldUser emptyUser = new CsldUser();
        emptyUser.setAuthorOf(new ArrayList<Game>());
        emptyUser.setCommented(new ArrayList<Comment>());
        emptyUser.setImage(null);
        emptyUser.setPerson(Person.getEmptyPerson());
        emptyUser.setPlayedGames(new ArrayList<UserPlayedGame>());
        emptyUser.setRole(CsldRoles.USER.getRole());
        return emptyUser;
    }
}
