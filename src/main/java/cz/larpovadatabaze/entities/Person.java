package cz.larpovadatabaze.entities;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompletable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Entity
@Table(schema = "public", name="csld_person")
public class Person implements Serializable, IAutoCompletable {
    private Integer id;

    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_key_gen")
    @SequenceGenerator(name = "id_key_gen", sequenceName = "csld_person_id_seq", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    @Column(name = "city", nullable = true, insertable = true, updatable = true, length = 2147483647, precision = 0)
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

    private List<CsldUser> users;

    @OneToMany(mappedBy = "person")
    public List<CsldUser> getUsers() {
        return users;
    }

    public void setUsers(List<CsldUser> users) {
        this.users = users;
    }

    @Override
    @Transient
    public String getAutoCompleteData() {
        return getEmail();
    }

    @Transient
    public Integer getAge() {
        java.util.Date birthDate = getBirthDate();
        Calendar now = Calendar.getInstance();
        Calendar birthDateCal = Calendar.getInstance();
        birthDateCal.setTime(birthDate);

        Integer age = now.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR);
        return age;
    }
}
