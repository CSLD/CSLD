package cz.larpovadatabaze.entities;

import org.apache.wicket.markup.html.form.upload.FileUpload;

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

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
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

    @Transient
    private List<FileUpload> fileUpload;

    @Transient
    public List<FileUpload> getFileUpload() {
        return fileUpload;
    }

    @Transient
    public void setFileUpload(List<FileUpload> fileUpload) {
        this.fileUpload = fileUpload;
    }

    @Transient
    public static Image getDefaultGroup() {
        Image groupImage = new Image();
        groupImage.setPath("upload/group_icon.png");
        return groupImage;
    }

    @Transient
    public static Image getDefaultUser() {
        Image userImage = new Image();
        userImage.setPath("upload/author_icon.png");
        return userImage;
    }

    @Transient
    public static Image getDefaultGame() {
        Image gameImage = new Image();
        gameImage.setPath("upload/question_icon_game.png");
        return gameImage;
    }

    public static String getPlusIconPath() {
        return "img/icon/plus_icon.png";
    }

    public static String getChartsIconPath() {
        return "img/icon/charts_icon.png";
    }

    public static String getRatingsIconPath() {
        return "img/icon/star_icon.png";
    }

    public static String getCommentsIconPath() {
        return "img/icon/comment_icon.png";
    }

    public static String getUserIconPath() {
        return "img/icon/user_icon.png";
    }

    public static String getAuthorIconPath() {
        return "img/icon/author_icon.png";
    }

    public static String getSettingsIconPath() {
        return "img/icon/settings_icon.png";
    }
}
