package cz.larpovadatabaze.entities;

import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 *
 */
@javax.persistence.Table(name = "csld_group_has_members")
@Entity
public class GroupHasMember implements Serializable {
    private int id;

    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_group_has_members_id_seq", name="id_gen")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Date start;

    @javax.persistence.Column(name="from_date", nullable=false, insertable= true, updatable=true, length = 10, precision = 0)
    @Type(type="date")
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    private Date end;

    @javax.persistence.Column(name="to_date", nullable=true, insertable= true, updatable=true, length = 10, precision = 0)
    @Type(type="date")
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupHasMember that = (GroupHasMember) o;

        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);

        return result;
    }

    private CsldUser user;

    @ManyToOne
    @javax.persistence.JoinColumn(
            name = "user_id",
            referencedColumnName = "`id`",
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

    private CsldGroup group;

    @ManyToOne
    @javax.persistence.JoinColumn(
            name = "group_id",
            referencedColumnName = "`id`",
            nullable = false,
            insertable = false,
            updatable = false
    )
    public CsldGroup getGroup() {
        return group;
    }

    public void setGroup(CsldGroup group) {
        this.group = group;
    }
}