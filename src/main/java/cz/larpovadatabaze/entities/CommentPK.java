package cz.larpovadatabaze.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Embeddable
public class CommentPK implements Serializable {
    private Integer userId;

    @Id
    @Column(
            name = "user_id",
            nullable = false,
            insertable = true,
            updatable = false
    )
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private Integer gameId;

    @Id
    @Column(
            name = "game_id",
            nullable = false,
            insertable = true,
            updatable = false
    )
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentPK commentPK = (CommentPK) o;

        if (gameId != null ? !gameId.equals(commentPK.gameId) : commentPK.gameId != null) return false;
        if (userId != null ? !userId.equals(commentPK.userId) : commentPK.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (gameId != null ? gameId.hashCode() : 0);
        return result;
    }
}
