package cz.larpovadatabaze.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 14:01
 */
@javax.persistence.IdClass(UserPlayedGamePK.class)
@javax.persistence.Table(name = "csld_user_played_game", schema = "public", catalog = "")
@Entity
public class UserPlayedGame implements Serializable {
    private Integer gameId;

    @javax.persistence.Column(name = "game_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    private Integer userId;

    @javax.persistence.Column(name = "user_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private Integer state;

    @javax.persistence.Column(name = "state", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPlayedGame that = (UserPlayedGame) o;

        if (gameId != null ? !gameId.equals(that.gameId) : that.gameId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gameId != null ? gameId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);

        return result;
    }

    private CsldUser playerOfGame;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public CsldUser getPlayerOfGame() {
        return playerOfGame;
    }

    public void setPlayerOfGame(CsldUser playerOfGame) {
        this.playerOfGame = playerOfGame;
    }

    private Game playedBy;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public Game getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(Game playedBy) {
        this.playedBy = playedBy;
    }
}
