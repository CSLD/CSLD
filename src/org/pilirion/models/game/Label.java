package org.pilirion.models.game;

public class Label {
    private String name;
    private String description;
    private int id;
    private boolean requires;
    private boolean isAuthorized;
    private int userId;

    public Label(int id, String name, String description, boolean requires, boolean isAuthorized, int userId){
        this.name = name;
        this.description = description;
        this.id = id;
        this.requires = requires;
        this.isAuthorized = isAuthorized;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public boolean isRequires() {
        return requires;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public int getUserId(){
        return userId;
    }
}
