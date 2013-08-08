package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Entity
@Table(schema = "public", name="csld_image")
public class Image implements Serializable {
    private Integer id;

    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_image_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String path;

    @Column(name = "path", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (id != null ? !id.equals(image.id) : image.id != null) return false;
        if (path != null ? !path.equals(image.path) : image.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    private List<Game> games;

    @OneToMany(mappedBy = "image")
    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    private List<CsldGroup> groups;

    @OneToMany(mappedBy = "image")
    public List<CsldGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CsldGroup> groups) {
        this.groups = groups;
    }

    private List<Photo> photos;

    @OneToMany(mappedBy = "image")
    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    private List<CsldUser> users;

    @OneToMany(mappedBy = "image")
    public List<CsldUser> getUsers() {
        return users;
    }

    public void setUsers(List<CsldUser> users) {
        this.users = users;
    }

    public static Image getDefaultGroup() {
        Image groupImage = new Image();
        groupImage.setPath("files/img/group_icon.png");
        return groupImage;
    }

    public static Image getDefaultUser() {
        Image userImage = new Image();
        userImage.setPath("files/img/author_icon.png");
        return userImage;
    }

    public static Image getDefaultGame() {
        Image gameImage = new Image();
        gameImage.setPath("files/img/question_icon.png");
        return gameImage;
    }

    public static String getPlusIconPath() {
        return "/files/img/icon/plus_icon.png";
    }

    public static String getQuestionIconPath() {
        return "/files/img/icon/question_icon.png";
    }

    public static String getLightBulbIconPath() {
        return "/files/img/icon/light_icon.png";
    }

    public static String getChartsIconPath() {
        return "/files/img/icon/charts_icon.png";
    }

    public static String getRatingsIconPath() {
        return "/files/img/icon/star_icon.png";
    }

    public static String getCommentsIconPath() {
        return "/files/img/icon/comment_icon.png";
    }

    public static String getUserIconPath() {
        return "/files/img/icon/user_icon.png";
    }

    public static String getSearchResultsIconPath() {
        return "/files/img/icon/glass_icon.png";
    }
}
