package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.api.Identifiable;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Entity
@Table(schema = "public", name="csld_label")
public class Label implements Serializable, IAutoCompletable, Identifiable<Integer> {
    private Integer id;

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_game_label_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String name;

    @Column(
            name = "name",
            nullable = false,
            insertable = true,
            updatable = true,
            length = 2147483647
    )
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    @Column(
            name = "description",
            nullable = true,
            insertable = true,
            updatable = true,
            length = 2147483647
    )
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Boolean isRequired;

    @Column(
            name = "is_required",
            nullable = true,
            insertable = true,
            updatable = true
    )
    @Basic
    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    private Boolean isAuthorized;

    @Column(
            name = "is_authorized",
            nullable = true,
            insertable = true,
            updatable = true
    )
    @Basic
    public Boolean getAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(Boolean authorized) {
        isAuthorized = authorized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (description != null ? !description.equals(label.description) : label.description != null) return false;
        if (id != null ? !id.equals(label.id) : label.id != null) return false;
        if (isAuthorized != null ? !isAuthorized.equals(label.isAuthorized) : label.isAuthorized != null) return false;
        if (isRequired != null ? !isRequired.equals(label.isRequired) : label.isRequired != null) return false;
        if (name != null ? !name.equals(label.name) : label.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (isRequired != null ? isRequired.hashCode() : 0);
        result = 31 * result + (isAuthorized != null ? isAuthorized.hashCode() : 0);
        return result;
    }

    private List<Game> games;

    @ManyToMany(mappedBy = "labels")
    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    private CsldUser addedBy;

    @ManyToOne
    @JoinColumn(
            name = "added_by",
            referencedColumnName = "id",
            nullable = false,
            insertable = true,
            updatable = true)
    public CsldUser getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(CsldUser addedBy) {
        this.addedBy = addedBy;
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getName();
    }

    @Transient
    private boolean selected;

    @Transient
    public void select(){
        selected = !selected;
    }

    @Transient
    public boolean isSelected(){
        return selected;
    }



    public static Label getEmptyLabel() {
        Label emptyLabel = new Label();
        emptyLabel.setGames(new ArrayList<Game>());
        return emptyLabel;
    }
}
