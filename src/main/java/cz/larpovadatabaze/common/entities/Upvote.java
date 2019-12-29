package cz.larpovadatabaze.common.entities;

import cz.larpovadatabaze.common.api.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Table(name = "csld_comment_upvote")
@Entity
public class Upvote implements Identifiable<Integer>, Serializable {
    public Upvote() {
    }

    public Upvote(CsldUser user, Comment comment) {
        this.user = user;
        this.comment = comment;
        this.added = new Timestamp(new java.util.Date().getTime());
    }

    private Integer id;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen_upvote")
    @SequenceGenerator(sequenceName = "csld_plus_one_id_seq", name = "id_gen_upvote", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    private Comment comment;

    @ManyToOne
    @JoinColumn(
            name = "comment_id",
            referencedColumnName = "`id`"
    )
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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
}