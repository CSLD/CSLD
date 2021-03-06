package cz.larpovadatabaze.common.entities;

import cz.larpovadatabaze.common.Identifiable;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Entity
@Table(name = "csld_photo")
public class Photo implements Identifiable<Integer>, Serializable, IEntityWithImage {
    private Integer id;

    @Column(name = "id", nullable = false, length = 10)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_photo")
    @SequenceGenerator(sequenceName = "csld_photo_id_seq", name = "id_gen_photo", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer author;

    @Column(name = "author", length = 10)
    @Basic
    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    private Integer orderSeq;

    @Column(name = "orderseq", length = 10)
    @Basic
    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer version) {
        this.orderSeq = version;
    }

    private String description;

    @Column(name="description")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int fullWidth;

    @Column(name="fullwidth")
    @Basic
    public int getFullWidth() {
        return fullWidth;
    }

    public void setFullWidth(int fullWidth) {
        this.fullWidth = fullWidth;
    }

    private int fullHeight;

    @Column(name="fullheight")
    @Basic
    public int getFullHeight() {
        return fullHeight;
    }

    public void setFullHeight(int fullHeight) {
        this.fullHeight = fullHeight;
    }

    private boolean featured;

    @Column(name="featured")
    @Basic
    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (author != null ? !author.equals(photo.author) : photo.author != null) return false;
        if (id != null ? !id.equals(photo.id) : photo.id != null) return false;
        if (orderSeq != null ? !orderSeq.equals(photo.orderSeq) : photo.orderSeq != null) return false;
        if (fullWidth != photo.fullWidth) return false;
        if (fullHeight != photo.fullHeight) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (orderSeq != null ? orderSeq.hashCode() : 0);
        result = 31 * result + fullWidth;
        result = 31 * result + fullHeight;
        return result;
    }

    private Game game;

    @ManyToOne()
    @JoinColumn(
            name="game",
            referencedColumnName = "`id`",
            nullable = false
    )
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private Image image;

    @ManyToOne
    @JoinColumn(
            name = "image",
            referencedColumnName = "`id`",
            nullable = false
    )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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
}
