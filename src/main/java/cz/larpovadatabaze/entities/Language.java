package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LocaleProvider;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 *
 */
@Entity
@Table(schema="public", name = "csld_language")
public class Language implements Serializable {
    @Column(name = "language",nullable = false)
    @Id
    private Locale language;
    @ManyToMany(mappedBy = "userHasLanguages")
    private List<CsldUser> users;
    @OneToMany(mappedBy = "language")
    private List<GameHasLanguages> gameHasLanguages;
    @OneToMany(mappedBy = "language")
    private List<LabelHasLanguages> labelHasLanguages;

    public Language(Locale language) {
        this.language = language;
    }

    public Language(String language) {
        LocaleProvider provider = new CodeLocaleProvider();
        this.language = provider.transformToLocale(language);
    }

    public Language() {
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public List<CsldUser> getUsers() {
        return users;
    }

    public void setUsers(List<CsldUser> users) {
        this.users = users;
    }

    public List<GameHasLanguages> getGameHasLanguages() {
        return gameHasLanguages;
    }

    public void setGameHasLanguages(List<GameHasLanguages> gameHasLanguages) {
        this.gameHasLanguages = gameHasLanguages;
    }

    public List<LabelHasLanguages> getLabelHasLanguages() {
        return labelHasLanguages;
    }

    public void setLabelHasLanguages(List<LabelHasLanguages> labelHasLanguages) {
        this.labelHasLanguages = labelHasLanguages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language1 = (Language) o;

        if (language == null && language1.getLanguage() != null) return false;
        if (!language.equals(language1.language)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return language.hashCode();
    }
}
