package org.pilirion.models.user;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 5.7.12
 * Time: 20:14
 */
public class Rating {
    private int userId = -1;
    private int rating = -1;
    private int id = -1;
    private String description;

    public Rating(int id, int userId, int rating, String description){
        this.userId = userId;
        this.rating = rating;
        this.id = id;
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public int getId(){
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getRating() {
        return rating;
    }

    public boolean validate(){
        return (getRating() != -1 && getUserId() != -1 && getDescription() != null);
    }
}
