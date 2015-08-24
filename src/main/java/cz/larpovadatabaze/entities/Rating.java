package cz.larpovadatabaze.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

/**
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Table(name="csld_rating")
@Entity
public class Rating implements IGameWithRating, Serializable {
    private int id;

    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_rating_id_seq", name="id_gen")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Integer rating;

    @Column(name = "rating", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    private Timestamp added;

    @Column(
            name = "added",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Basic
    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rating rating1 = (Rating) o;

        if (game != null ? !game.equals(rating1.game) : rating1.game != null) return false;
        if (rating != null ? !rating.equals(rating1.rating) : rating1.rating != null) return false;
        if (user != null ? !user.equals(rating1.user) : rating1.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (game != null ? game.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        return result;
    }

    private Game game;

    @ManyToOne
    @JoinColumn(
            name = "game_id",
            referencedColumnName = "`id`"
    )
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private CsldUser user;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "`id`"
    )
    public CsldUser getUser() {
        return user;
    }

    public void setUser(CsldUser user) {
        this.user = user;
    }

    public static String getColorOf(Double gameAsAverageRating){
        double gameAverage;
        if(gameAsAverageRating == null) {
            gameAverage = 0;
        } else {
            gameAverage = gameAsAverageRating;
        }
        String gameRatingColor = "notrated";
        if(gameAverage > 0){
            gameRatingColor = "mediocre";
        }
        if(gameAverage > 40) {
            gameRatingColor = "average";
        }
        if(gameAverage > 70) {
            gameRatingColor = "great";
        }
        return gameRatingColor;
    }
}
