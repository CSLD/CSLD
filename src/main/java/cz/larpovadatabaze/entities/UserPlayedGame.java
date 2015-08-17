package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 14:01
 */
@javax.persistence.Table(name = "csld_user_played_game")
@Entity
public class UserPlayedGame implements Serializable {
    public static final int STATE_CODE_PLAYED = 2;
    public static final int STATE_CODE_WANT_TO_PLAY = 1;
    public static final int STATE_CODE_NONE = 0;

    public static enum UserPlayedGameState {
        NONE(STATE_CODE_NONE),
        PLAYED(STATE_CODE_PLAYED),
        WANT_TO_PLAY(STATE_CODE_WANT_TO_PLAY);

        private final int dbCode;

        private UserPlayedGameState(int dbCode) { this.dbCode = dbCode; }
    }

    private int id;

    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_user_played_game_id_seq", name="id_gen")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private UserPlayedGameState state = UserPlayedGameState.NONE;

    @javax.persistence.Column(name = "state", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    public Integer getState() {
        return state.dbCode;
    }

    public void setState(Integer state) {
        switch(state) {
            case STATE_CODE_NONE:
                this.state = UserPlayedGameState.NONE;
                break;
            case STATE_CODE_PLAYED:
                this.state = UserPlayedGameState.PLAYED;
                break;
            case STATE_CODE_WANT_TO_PLAY:
                this.state = UserPlayedGameState.WANT_TO_PLAY;
                break;
        }

        // Sanity check
        if (this.state.dbCode != state) {
            throw new IllegalArgumentException("Invalid state code or configuration error");
        }
    }

    @Transient
    public UserPlayedGameState getStateEnum() {
        return this.state;
    }

    public void setStateEnum(UserPlayedGameState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPlayedGame that = (UserPlayedGame) o;

        if (state != null ? !state.equals(that.state) : that.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;

        return result;
    }

    private CsldUser playerOfGame;

    @ManyToOne
    @javax.persistence.JoinColumn(
            name = "user_id",
            referencedColumnName = "`id`",
            nullable = false,
            insertable = false,
            updatable = false
    )
    public CsldUser getPlayerOfGame() {
        return playerOfGame;
    }

    public void setPlayerOfGame(CsldUser playerOfGame) {
        this.playerOfGame = playerOfGame;
    }

    private Game game;

    @ManyToOne
    @javax.persistence.JoinColumn(
            name = "game_id",
            referencedColumnName = "`id`",
            nullable = false,
            insertable = false,
            updatable = false
    )
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public static Integer getStateForDb(String state){
        if(state.equals("Nehrál jsem")) {
            return 0;
        }
        if(state.equals("Chci hrát")) {
            return 1;
        }
        if(state.equals("Hrál jsem")) {
            return 2;
        }
        return 0;
    }

    public static String getStateFromDb(int state){
        if(state == 2) {
            return "Hrál jsem";
        } else if(state == 1) {
            return "Chci hrát";
        } else {
            return "Nehrál jsem";
        }
    }
}
