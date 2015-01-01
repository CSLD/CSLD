package cz.larpovadatabaze.entities;

import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 */
@javax.persistence.IdClass(GroupHasMemberPK.class)
@javax.persistence.Table(name = "csld_group_has_members", schema = "public", catalog = "")
@Entity
public class GroupHasMember implements Serializable {
    private Integer groupId;

    @javax.persistence.Column(name = "group_id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);

        return result;
    }

    private CsldUser user;

    @ManyToOne
    @javax.persistence.JoinColumn(
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
        this.userId = (user==null)?null:user.getId();
    }

    private CsldGroup group;

    @ManyToOne
    @javax.persistence.JoinColumn(
            name = "group_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    public CsldGroup getGroup() {
        return group;
    }

    public void setGroup(CsldGroup group) {
        this.group = group;
        this.groupId = (group==null)?null:group.getId();
    }
}
