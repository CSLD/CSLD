package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.api.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Table(name = "csld_csld_user", schema = "public", catalog = "")
@Entity
public class CsldUser implements Serializable, Identifiable {
    private Integer id;

    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_user_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer imageId;

    @Column(name = "image", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    private String password;

    @Column(name = "password", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Short role;

    @Column(name = "role", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
    }

    private Integer personId;

    @Column(name = "person", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    private Integer fbUser;

    @Column(name = "fb_user", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getFbUser() {
        return fbUser;
    }

    public void setFbUser(Integer fbUser) {
        this.fbUser = fbUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsldUser csldUser = (CsldUser) o;

        if (fbUser != null ? !fbUser.equals(csldUser.fbUser) : csldUser.fbUser != null) return false;
        if (id != null ? !id.equals(csldUser.id) : csldUser.id != null) return false;
        if (image != null ? !image.equals(csldUser.image) : csldUser.image != null) return false;
        if (password != null ? !password.equals(csldUser.password) : csldUser.password != null) return false;
        if (personId != null ? !personId.equals(csldUser.personId) : csldUser.personId != null) return false;
        if (role != null ? !role.equals(csldUser.role) : csldUser.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (personId != null ? personId.hashCode() : 0);
        result = 31 * result + (fbUser != null ? fbUser.hashCode() : 0);
        return result;
    }

    private FbUser fb_user;

    @OneToOne
    @JoinColumn(name = "fb_user", referencedColumnName = "id_csld_user", insertable = false, updatable = false)
    public FbUser getFb_user() {
        return fb_user;
    }

    public void setFb_user(FbUser fb_user) {
        this.fb_user = fb_user;
    }

    private Person person;

    @ManyToOne
    @JoinColumn(name = "person", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private List<Game> authorOf;

    @ManyToMany(mappedBy = "authors")
    public List<Game> getAuthorOf() {
        return authorOf;
    }

    public void setAuthorOf(List<Game> authorOf) {
        this.authorOf = authorOf;
    }

    private List<CsldGroup> administersGroups;

    @ManyToMany(mappedBy = "administrators")
    public List<CsldGroup> getAdministersGroups() {
        return administersGroups;
    }

    public void setAdministersGroups(List<CsldGroup> administersGroups) {
        this.administersGroups = administersGroups;
    }

    private List<UserPlayedGame> playedGames;

    @OneToMany(mappedBy = "playerOfGame")
    public List<UserPlayedGame> getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(List<UserPlayedGame> playedGames) {
        this.playedGames = playedGames;
    }

    private List<Label> labelsAuthor;

    @OneToMany(mappedBy = "addedBy")
    public List<Label> getLabelsAuthor() {
        return labelsAuthor;
    }

    public void setLabelsAuthor(List<Label> labelsAuthor) {
        this.labelsAuthor = labelsAuthor;
    }

    private List<EmailAuthentication> emailAuthentications;

    @OneToMany(mappedBy = "user")
    public List<EmailAuthentication> getEmailAuthentications() {
        return emailAuthentications;
    }

    public void setEmailAuthentications(List<EmailAuthentication> emailAuthentications) {
        this.emailAuthentications = emailAuthentications;
    }

    private List<Rating> rated;

    @OneToMany(mappedBy = "user")
    public List<Rating> getRated() {
        return rated;
    }

    public void setRated(List<Rating> rated) {
        this.rated = rated;
    }

    private List<Comment> commented;

    @OneToMany(mappedBy = "user")
    public List<Comment> getCommented() {
        return commented;
    }

    public void setCommented(List<Comment> commented) {
        this.commented = commented;
    }

    private List<GroupHasMember> groupMembers;

    @OneToMany(mappedBy = "user")
    public List<GroupHasMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupHasMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    private Image image;

    @ManyToOne
    @JoinColumn(name = "image", referencedColumnName = "id", insertable = false, updatable = false)
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Transient
    private Game bestGame;

    @Transient
    public Game getBestGame(int amountOfRatings, double averageRating){
        double bestRating = 0;
        Game bestGame = null;
        for(Game game: authorOf){
            int amountOfRatingsSingle = game.getRatings().size();
            double averageRatingForGame = game.getAverageRating();

            if(amountOfRatingsSingle > 0){
                double actualBestRating = (averageRatingForGame + (averageRating/ amountOfRatings * 8)) /
                    (amountOfRatingsSingle + 12);
                if(actualBestRating > bestRating){
                    bestRating = actualBestRating;
                    bestGame = game;
                }
            } else {
                if(bestRating == 0){
                    bestGame = game;
                }
            }

        }
        return bestGame;
    }

    @Transient
    public void setBestGame(Game bestGame){
        this.bestGame = bestGame;
    }

    @Transient
    public Rating getRatingOfGame(Integer gameId){
        for(Rating rating: rated){
            if(rating.getGameId() == gameId){
                return rating;
            }
        }
        return null;
    }
}
