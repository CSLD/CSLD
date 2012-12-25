package org.pilirion.models.user;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.6.12
 * Time: 21:34
 */
public class Resource {
    private int id;
    private String path;
    private String aDefault;

    public Resource(int id, String path, String aDefault){
        this.id = id;
        this.path = path;
        this.aDefault = aDefault;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getDefault() {
        return aDefault;
    }
}
