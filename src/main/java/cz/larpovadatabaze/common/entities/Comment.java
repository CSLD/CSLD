package cz.larpovadatabaze.common.entities;

import cz.larpovadatabaze.common.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Table(name = "csld_comment")
@Entity
public class Comment implements Identifiable<Integer>, Serializable {
    public Comment() {
    }

    public Comment(Integer id, String comment, Timestamp added) {
        this.id = id;
        this.comment = comment;
        this.added = added;
    }

    public Comment(CsldUser user, Game game, String commentText) {
        this(user, game, commentText, false);
    }

    private Integer id;

    public Comment(CsldUser user, Game game, String commentText, boolean hidden) {
        this.game = game;
        this.comment = commentText;
        this.user = user;
        this.added = Timestamp.from(Instant.now());
        this.isHidden = hidden;
        this.pluses = new ArrayList<>();
    }

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_comment")
    @SequenceGenerator(sequenceName = "csld_comment_id_seq", name = "id_gen_comment", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String comment;

    @Column(name = "comment", nullable = false)
    @Basic
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private Timestamp added;

    @Column(name = "added", nullable = false)
    @Basic
    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }

    private Boolean isHidden = Boolean.FALSE;

    @Column(name = "is_hidden")
    @Basic
    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    private Integer amountOfUpvotes = 0;

    @Column(name = "amount_of_upvotes")
    @Basic
    public Integer getAmountOfUpvotes() {
        return amountOfUpvotes;
    }

    public void setAmountOfUpvotes(Integer amountOfUpvotes) {
        this.amountOfUpvotes = amountOfUpvotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment1 = (Comment) o;

        if (comment != null ? !comment.equals(comment1.comment) : comment1.comment != null) return false;
        if (isHidden != null ? !isHidden.equals(comment1.isHidden) : comment1.isHidden != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = comment != null ? comment.hashCode() : 0;
        result = 31 * result + (isHidden != null ? isHidden.hashCode() : 0);
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

    private List<Upvote> pluses = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "comment_id", nullable = false)
    public List<Upvote> getPluses() {
        return pluses;
    }

    public void setPluses(List<Upvote> pluses) {
        this.pluses = pluses;
    }
}
