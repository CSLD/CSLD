package cz.larpovadatabaze.entities;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;

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
@Table(schema = "public", name="csld_photo")
public class Photo implements Serializable {
    private Integer id;

    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer imageId;

    @Column(name = "image", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    private Integer author;

    @Column(name = "author", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    private Integer version;

    @Column(name = "version", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (author != null ? !author.equals(photo.author) : photo.author != null) return false;
        if (id != null ? !id.equals(photo.id) : photo.id != null) return false;
        if (imageId != null ? !imageId.equals(photo.imageId) : photo.imageId != null) return false;
        if (version != null ? !version.equals(photo.version) : photo.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    private List<Game> games;

    @ManyToMany(mappedBy = "photos")
    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    private Image image;

    @ManyToOne
    @JoinColumn(name = "image", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
