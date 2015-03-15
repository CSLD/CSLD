package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 */
@IdClass(CommentPK.class)
@Table(schema = "public", name="csld_comment")
@Entity
public class Comment implements Serializable {
    private Integer userId;

    @Column(name = "user_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private Integer gameId;

    @Column(name = "game_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    private String comment;

    @Column(
            name = "comment",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Basic
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    private Boolean isHidden = Boolean.FALSE;

    @Column(name="is_hidden")
    @Basic
    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    private String lang;

    @Column(name = "lang")
    @Basic
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment1 = (Comment) o;

        if (comment != null ? !comment.equals(comment1.comment) : comment1.comment != null) return false;
        if (gameId != null ? !gameId.equals(comment1.gameId) : comment1.gameId != null) return false;
        if (userId != null ? !userId.equals(comment1.userId) : comment1.userId != null) return false;
        if (isHidden != null ? !isHidden.equals(comment1.isHidden) : comment1.isHidden != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (gameId != null ? gameId.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (isHidden != null ? isHidden.hashCode() : 0);
        return result;
    }

    private Game game;

    @ManyToOne
    @JoinColumn(
            name = "game_id",
            referencedColumnName = "id",
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

    private CsldUser user;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    public CsldUser getUser() {
        return user;
    }

    public void setUser(CsldUser user) {
        this.user = user;
    }
}
