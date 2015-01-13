package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.lang.*;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 */
@Entity
@Table(schema = "public", name="csld_label")
public class Label implements Serializable, IAutoCompletable, Identifiable<Integer>, TranslatableEntity {
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

    @Transient
    private LabelHasLanguages defaultLanguage = new LabelHasLanguages();

    @Transient
    private String name;

    @Transient
    public String getName() {
        if(name == null) {
            new TranslatableEntityTranslator(new DbSessionLanguageSolver()).translate(this);
        }
        return name;
    }

    @Transient
    public void setName(String name) {
        this.name = name;
        defaultLanguage.setName(name);
    }

    @Transient
    private String description;

    @Transient
    public String getDescription() {
        if(description == null) {
            new TranslatableEntityTranslator(new DbSessionLanguageSolver()).translate(this);
        }
        return description;
    }

    @Transient
    public void setDescription(String description) {
        this.description = description;
        defaultLanguage.setDescription(description);
    }
    @Transient
    private String lang;

    @Transient
    public String getLang() {
        if(lang == null) {
            new TranslatableEntityTranslator(new DbSessionLanguageSolver()).translate(this);
        }
        return lang;
    }

    @Transient
    public void setLang(String lang) {
        this.lang = lang;
        if(getLabelHasLanguages()  == null) {
            setLabelHasLanguages(new ArrayList<LabelHasLanguages>());
        }
        LocaleProvider provider = new CodeLocaleProvider();
        Locale actualLanguage = provider.transformToLocale(lang);
        for(LabelHasLanguages language: getLabelHasLanguages()) {
            // Ignore already added language.
            if(language.getLanguage().getLanguage().equals(actualLanguage)){
                return;
            }
        }

        defaultLanguage.setLabel(this);
        defaultLanguage.setLanguage(new Language(lang));
        getLabelHasLanguages().add(defaultLanguage);
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

    @SuppressWarnings("RedundantIfStatement")
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

    private List<LabelHasLanguages> labelHasLanguages;

    @OneToMany(mappedBy = "label")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public List<LabelHasLanguages> getLabelHasLanguages() {
        return labelHasLanguages;
    }

    public void setLabelHasLanguages(List<LabelHasLanguages> labelHasLanguages) {
        this.labelHasLanguages = labelHasLanguages;
    }

    @Transient
    public List<TranslationEntity> getLanguages() {
        if(labelHasLanguages == null) {
            return null;
        }
        return new ArrayList<TranslationEntity>(labelHasLanguages);
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getName();
    }

    @Transient
    private boolean selected = false;

    @Transient
    public void select(){
        selected = !selected;
    }

    @Transient
    public boolean isSelected(){
        return selected;
    }



    public static Label getEmptyLabel() {
        return new Label();
    }

    @Transient
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
