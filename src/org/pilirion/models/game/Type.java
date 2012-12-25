package org.pilirion.models.game;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 13:55
 */
public class Type {
    private String name;
    private int id;

    public Type(int id, String name){
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }
}
