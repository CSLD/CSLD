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
@Table(schema = "public", name="csld_csld_group")
public class CsldGroup implements Serializable {
    private Integer id;

    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_group_id_seq", allocationSize = 1)
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

    private String name;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsldGroup group = (CsldGroup) o;

        if (id != null ? !id.equals(group.id) : group.id != null) return false;
        if (imageId != null ? !imageId.equals(group.imageId) : group.imageId != null) return false;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    private List<Game> authorsOf;

    @ManyToMany(mappedBy = "groupAuthor")
    public List<Game> getAuthorsOf() {
        return authorsOf;
    }

    public void setAuthorsOf(List<Game> authorsOf) {
        this.authorsOf = authorsOf;
    }

    private List<CsldUser> administrators;

    @JoinTable(name = "csld_group_has_administrator", catalog = "", schema = "public", joinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id", nullable = false), inverseJoinColumns = @JoinColumn(name = "id_csld_user", referencedColumnName = "id", nullable = false))
    @ManyToMany
    public List<CsldUser> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<CsldUser> administrators) {
        this.administrators = administrators;
    }

    private Image image;

    @ManyToOne
    @JoinColumn(name = "image", referencedColumnName = "id", insertable = false, updatable = false)
    public Image getImage() {
        return image != null ? image : Image.getDefaultGroup();
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private List<GroupHasMember> members;

    @OneToMany(mappedBy = "group")
    public List<GroupHasMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupHasMember> members) {
        this.members = members;
    }
}
