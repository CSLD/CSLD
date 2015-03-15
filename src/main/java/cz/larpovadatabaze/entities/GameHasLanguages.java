package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.lang.TranslationEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "csld_game_has_languages", schema = "public")
public class GameHasLanguages implements Serializable, TranslationEntity {
    private Integer id;
    private String name;
    private String description;
    private Game game;
    private Language language;

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_game_has_languages_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name="description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_game", referencedColumnName = "id", nullable = false)
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "language", referencedColumnName = "language", nullable = false)
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language languageForGame) {
        this.language = languageForGame;
    }
}
