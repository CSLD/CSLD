package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Entity for database representation of News.
 */
@Entity
@Table(name = "csld_news")
public class News implements Serializable {
    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_news_id_seq", allocationSize = 1)
    private Integer id;
    @Column(name = "text")
    @Basic
    private String text;
    @Column(name = "added")
    private Timestamp added;
    @Column(name = "lang")
    @Basic
    private String lang;
    @ManyToOne
    @JoinColumn(name="author_id")
    private CsldUser author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CsldUser getAuthor() {
        return author;
    }

    public void setAuthor(CsldUser author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
