package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.components.common.multiac.IAutoCompletable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Entity
@Table(name = "csld_csld_group")
public class CsldGroup implements Serializable, Identifiable, IAutoCompletable, IEntityWithImage {
    private Integer id;

    @Column(name = "id", nullable = false, updatable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_group_id_seq", name = "id_gen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String name;

    @Column(name="name")
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
        if (name != null ? !name.equals(group.name) : group.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
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

    private CsldUser administrator;

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name="administrator_id",
            referencedColumnName = "`id`"
    )
    @Cascade(CascadeType.SAVE_UPDATE)
    public CsldUser getAdministrator() {
        return administrator;
    }

    public void setAdministrator(CsldUser administrator) {
        this.administrator = administrator;
    }

    private Image image;

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name = "image",
            referencedColumnName = "`id`",
            nullable = true,
            insertable = true,
            updatable = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    public Image getImage() {
        return image;
    }

    @Override
    @Transient
    public IPredefinedImage getDefaultImage() {
        return PredefinedImage.DEFAULT_GROUP_ICON;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getName();
    }

    /**
     * It creates correctly initialized empty group
     * No surprises included.
     *
     * @return Group with at least basic defaults.
     */
    public static CsldGroup getEmptyGroup() {
        CsldGroup emptyGroup = new CsldGroup();
        emptyGroup.setAuthorsOf(new ArrayList<>());
        return emptyGroup;
    }
}
