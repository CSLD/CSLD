package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.lang.*;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Entity
@Table(schema = "public", name="csld_csld_group")
public class CsldGroup implements Serializable, Identifiable, IAutoCompletable, IEntityWithImage, TranslatableEntity {
    private Integer id;

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = false
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_group_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    private GroupHasLanguage defaultLanguage = new GroupHasLanguage();

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
        if(getGroupHasLanguages()  == null) {
            setGroupHasLanguages(new ArrayList<GroupHasLanguage>());
        }
        LocaleProvider provider = new CodeLocaleProvider();
        Locale actualLanguage = provider.transformToLocale(lang);
        for(GroupHasLanguage language: getGroupHasLanguages()) {
            // Ignore already added language.
            if(language.getLanguage().getLanguage().equals(actualLanguage)){
                return;
            }
        }

        defaultLanguage.setGroup(this);
        defaultLanguage.setLanguage(new Language(lang));
        getGroupHasLanguages().add(defaultLanguage);
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

    private List<CsldUser> administrators;

    @JoinTable(
            name = "csld_group_has_administrator",
            schema = "public",
            joinColumns = @JoinColumn(
                    name = "id_group",
                    referencedColumnName = "id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "id_user",
                    referencedColumnName = "id",
                    nullable = false)
    )
    @ManyToMany
    @Cascade(CascadeType.SAVE_UPDATE)
    public List<CsldUser> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(List<CsldUser> administrators) {
        this.administrators = administrators;
    }

    private Image image;

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(
            name = "image",
            referencedColumnName = "id",
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

    private List<GroupHasMember> members;

    @OneToMany(mappedBy = "group")
    @Cascade(CascadeType.SAVE_UPDATE)
    public List<GroupHasMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupHasMember> members) {
        this.members = members;
    }

    private List<GroupHasLanguage> groupHasLanguages;

    @OneToMany(mappedBy = "group",fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public List<GroupHasLanguage> getGroupHasLanguages() {
        return groupHasLanguages;
    }

    public void setGroupHasLanguages(List<GroupHasLanguage> labelHasLanguages) {
        this.groupHasLanguages = labelHasLanguages;
    }

    @Transient
    public List<TranslationEntity> getLanguages() {
        if(groupHasLanguages == null) {
            return null;
        }
        return new ArrayList<TranslationEntity>(groupHasLanguages);
    }

    @Transient
    public String getDescription(){
        return "";
    }

    @Transient
    public void setDescription(String description) {}

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getName();
    }

    /**
     * It creates correctly initialized empty group
     * No surprises included.
     *
     * @return
     */
    public static CsldGroup getEmptyGroup() {
        CsldGroup emptyGroup = new CsldGroup();
        emptyGroup.setAdministrators(new ArrayList<CsldUser>());
        emptyGroup.setAuthorsOf(new ArrayList<Game>());
        emptyGroup.setMembers(new ArrayList<GroupHasMember>());
        return emptyGroup;
    }
}
