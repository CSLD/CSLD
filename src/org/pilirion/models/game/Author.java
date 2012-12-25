package org.pilirion.models.game;

import org.pilirion.models.user.Person;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 13:32
 */
public class Author {
    private int id = -1;
    private Person person;

    public Author(int id, Person person){
        this.id = id;
        this.person = person;
    }

    public boolean validate(){
        return (getId() != -1 && getPerson() != null);
    }

    public int getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }
}
