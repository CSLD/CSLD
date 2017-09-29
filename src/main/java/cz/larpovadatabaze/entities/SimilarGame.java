package cz.larpovadatabaze.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="similar_game")
public class SimilarGame {
    private Integer id;
    private Double similarity;
    private Integer idGame1;
    private Integer idGame2;

    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="similarity_coefficient")
    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    @Column(name="id_game_1")
    public Integer getIdGame1() {
        return idGame1;
    }

    public void setIdGame1(Integer idGame1) {
        this.idGame1 = idGame1;
    }

    @Column(name="id_game_2")
    public Integer getIdGame2() {
        return idGame2;
    }

    public void setIdGame2(Integer idGame2) {
        this.idGame2 = idGame2;
    }
}
