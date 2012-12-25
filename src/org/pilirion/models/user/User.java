package org.pilirion.models.user;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 13:05
 */
public class User {
    private String userName;
    private String password;
    private int id;
    private Role role;
    private Person person;
    private String description;

    public User(int id, Person person, String userName, String password, Role role){
        this.id = id;
        this.setUserName(userName);
        this.setPassword(password);
        this.setRole(role);
        this.setPerson(person);
    }

    public boolean validate(){
        return getPerson().validate() && getUserName() != null && getPassword() != null;
    }

    public int getId() {
        return id;
    }

    public String getPassword(){
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getUserName(){
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getDescription() {
        return description;
    }
}
