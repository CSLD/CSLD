package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@javax.persistence.Table(name = "csld_email_authentication", schema = "public", catalog = "")
@Entity
public class EmailAuthentication implements Serializable {
    private Integer id;

    @javax.persistence.Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_email_authentication_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String authToken;

    @javax.persistence.Column(name = "auth_token", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAuthentication that = (EmailAuthentication) o;

        if (authToken != null ? !authToken.equals(that.authToken) : that.authToken != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (authToken != null ? authToken.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    private CsldUser user;

    @ManyToOne
    @javax.persistence.JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            insertable = true
    )
    public CsldUser getUser() {
        return user;
    }

    public void setUser(CsldUser user) {
        this.user = user;
    }
}
