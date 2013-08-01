package cz.larpovadatabaze.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@javax.persistence.Table(name = "csld_fb_user", schema = "public", catalog = "")
@Entity
public class FbUser implements Serializable {
    private Integer id;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String fbToken;

    @javax.persistence.Column(name = "fb_token", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    private Integer idCsldUser;

    @javax.persistence.Column(name = "id_csld_user", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getIdCsldUser() {
        return idCsldUser;
    }

    public void setIdCsldUser(Integer idCsldUser) {
        this.idCsldUser = idCsldUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FbUser fbUser = (FbUser) o;

        if (fbToken != null ? !fbToken.equals(fbUser.fbToken) : fbUser.fbToken != null) return false;
        if (id != null ? !id.equals(fbUser.id) : fbUser.id != null) return false;
        if (idCsldUser != null ? !idCsldUser.equals(fbUser.idCsldUser) : fbUser.idCsldUser != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fbToken != null ? fbToken.hashCode() : 0);
        result = 31 * result + (idCsldUser != null ? idCsldUser.hashCode() : 0);
        return result;
    }
}
