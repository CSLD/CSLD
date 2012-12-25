package org.pilirion.models.user;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 8.7.12
 * Time: 9:34
 */
public class Person {
    private int id = -1;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String nickName;
    private String email;
    private String image;
    private String address;
    private String description;

    public Person(int id, String firstName, String lastName, Date birthDate, String nickName, String email, String image,
                  String address, String description){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setBirthDate(birthDate);
        this.setNickName(nickName);
        this.setEmail(email);
        this.setId(id);
        this.setImage(image);
        this.address = address;
        this.description = description;
    }

    public boolean validate() {
        return getFirstName() != null && !getFirstName().equals("") && getLastName() != null && !getLastName().equals("");
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return getFirstName() + " \"" + getNickName() + "\" " + getLastName();  //To change body of created methods use File | Settings | File Templates.
    }

    public String getNameDiff() {
        String nickName = getNickName();
        String firstName = getFirstName();
        String lastName = getLastName();
        if(nickName != null && !nickName.equals("")){
            return getFirstName() + " \"" + getNickName() + "\" " + getLastName();
        } else {
            return getFirstName() + " " + getLastName();
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public int getAge() {
        if(getBirthDate() != null){
            DateMidnight birthdate = new DateMidnight(getBirthDate().getTime());
            DateTime now = new DateTime();
            Years age = Years.yearsBetween(birthdate, now);
            return age.getYears();
        }
        return 0;
    }

    public String getAddress() {
        return address;
    }

    public String getFullName() {
        return getFirstName()+ " " + getLastName();
    }

    public String getDescription() {
        if(description == null){
            description = "";
        }
        return description;
    }
}
