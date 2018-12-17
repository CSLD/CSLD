package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "csld_plus_one")
@Entity
public class PlusOne implements Serializable {
    public PlusOne() {}

    private int id;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_plus_one_id_seq", name = "id_gen")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
