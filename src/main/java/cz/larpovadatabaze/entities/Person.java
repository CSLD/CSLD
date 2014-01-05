package cz.larpovadatabaze.entities;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Date;

/**
 *
 */
@Embeddable
public class Person implements Serializable, IAutoCompletable {
    private Integer id;

    private String name;

    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    @Column(name = "description", nullable = true, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String email;

    @Column(name = "email", nullable = false, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String nickname;

    @Column(name = "nickname", nullable = true, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private Date birthDate;

    @Column(name = "birth_date", nullable = true, insertable = true, updatable = true, length = 13, precision = 0)
    @Basic
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    private String city;

    @Column(name = "address", nullable = true, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (birthDate != null ? !birthDate.equals(person.birthDate) : person.birthDate != null) return false;
        if (city != null ? !city.equals(person.city) : person.city != null) return false;
        if (email != null ? !email.equals(person.email) : person.email != null) return false;
        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        if (nickname != null ? !nickname.equals(person.nickname) : person.nickname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        return result;
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        if(getEmail() == null){
            return null;
        }
        return String.format("%s %s, %s", getNickname(), getName(), getEmail());
    }

    @Transient
    public Integer getAge() {
        if(getBirthDate() == null){
            return null;
        }

        Interval life = new Interval(new DateTime(getBirthDate()), new DateTime());
        return life.toPeriod().getYears();
    }

    @Transient
    public String getNickNameView() {
        return (nickname != null && !nickname.equals("")) ? nickname : getName().split(" ")[0];
    }

    public static Person getEmptyPerson() {
        Person emptyPerson = new Person();
        return emptyPerson;
    }
}
