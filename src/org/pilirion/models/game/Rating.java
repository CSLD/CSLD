package org.pilirion.models.game;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 17:18
 */
public class Rating {
    private int rating = -1;
    private int id;
    private int authorId = -1;
    private int gameId = -1;

    public Rating(int id, int rating, int authorId, int gameId){
        this.rating = rating;
        this.id = id;
        this.authorId = authorId;
        this.gameId = gameId;
    }

    public int getAuthorId(){
        return authorId;
    }

    public int getId() {
        return id;
    }

    public int getRating(){
        return rating;
    }

    public int getGameId(){
        return gameId;
    }

    public boolean validate() {
        return (getAuthorId() != -1 && getGameId() != -1 && getRating() != -1);  //To change body of created methods use File | Settings | File Templates.
    }
}
