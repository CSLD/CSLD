package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.lang.TranslationEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 */
@Entity
@Table(name="csld_label_has_languages", schema = "public")
public class LabelHasLanguages implements Serializable, TranslationEntity {
    private Integer id;
    private String name;
    private String description;
    private Label label;
    private Language language;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_label_has_languages_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_label", referencedColumnName = "id", nullable = false)
    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    @ManyToOne(optional = false)
    @JoinColumn(name = "language", referencedColumnName = "language", nullable = false)
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
