package cz.larpovadatabaze.common.entities;

import cz.larpovadatabaze.common.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Table(name = "csld_rating")
@Entity
public class Rating implements Identifiable<Integer>, Serializable, IGameWithRating {
    public static final int STATE_CODE_PLAYED = 2;
    public static final int STATE_CODE_WANT_TO_PLAY = 1;
    public static final int STATE_CODE_NONE = 0;

    public enum GameState {
        NONE(STATE_CODE_NONE),
        WANT_TO_PLAY(STATE_CODE_WANT_TO_PLAY),
        PLAYED(STATE_CODE_PLAYED);

        private final int dbCode;

        GameState(int dbCode) {
            this.dbCode = dbCode;
        }
    }

    public Rating() {
    }

    public Rating(CsldUser user, Game game, int rating) {
        this(user, game, rating, GameState.NONE);
    }

    public Rating(CsldUser user, Game game, GameState state) {
        this(user, game, null, state);
    }

    public Rating(CsldUser user, Game game, Integer rating, GameState state) {
        this.added = Timestamp.from(Instant.now());
        this.game = game;
        this.user = user;
        this.rating = rating;
        this.state = state;
    }


    private Integer id;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_rating")
    @SequenceGenerator(sequenceName = "csld_rating_id_seq", name = "id_gen_rating", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer rating;

    @Column(name = "rating", length = 10)
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
            nullable = false
    )
    @Basic
    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }

    private GameState state = GameState.NONE;

    @Column(name = "state", nullable = false, length = 10)
    public Integer getState() {
        return state.dbCode;
    }

    public void setState(Integer state) {
        switch (state) {
            case STATE_CODE_NONE:
                this.state = GameState.NONE;
                break;
            case STATE_CODE_PLAYED:
                this.state = GameState.PLAYED;
                break;
            case STATE_CODE_WANT_TO_PLAY:
                this.state = GameState.WANT_TO_PLAY;
                break;
        }

        // Sanity check
        if (this.state.dbCode != state) {
            throw new IllegalArgumentException("Invalid state code or configuration error");
        }
    }

    @Transient
    public GameState getStateEnum() {
        return this.state;
    }

    public void setStateEnum(GameState state) {
        this.state = state;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating1 = (Rating) o;
        return Objects.equals(id, rating1.id) &&
                Objects.equals(rating, rating1.rating) &&
                Objects.equals(added, rating1.added) &&
                state == rating1.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, added, state);
    }
}
