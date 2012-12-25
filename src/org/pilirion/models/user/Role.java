package org.pilirion.models.user;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.6.12
 * Time: 21:30
 */
public class Role {
    private int id;
    private String name;

    public Role(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
