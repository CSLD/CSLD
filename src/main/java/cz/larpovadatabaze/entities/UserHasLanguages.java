package cz.larpovadatabaze.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Join table among user and languages they are interested in.
 */
@Entity
@Table(name="csld_user_has_languages")
public class UserHasLanguages implements Serializable {
    @Column(name = "id", nullable = false, insertable = true, updatable = true )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_user_has_languages_id_seq", allocationSize = 1)
    Integer id;
    @Basic
    @Column(name = "language")
    String language;
    @ManyToOne(optional = false)
    @JoinColumn(name="id_user")
    CsldUser user;

    public UserHasLanguages() {
    }

    public UserHasLanguages(String language) {
        this.language = language;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public CsldUser getUser() {
        return user;
    }

    public void setUser(CsldUser user) {
        this.user = user;
    }
}
