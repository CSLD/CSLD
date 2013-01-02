package org.pilirion.models.game;

import java.sql.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 20:47
 */
public class Comment {
    private int id;
    private Date date;
    private String text;
    private int userId = -1;
    private int gameId = -1;

    public Comment(int id, Date date, String text, int userId, int gameId){
        this.id = id;
        this.date = date;
        this.text = text;
        this.userId = userId;
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public int getUserId() {
        return userId;
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isValid(){
        if(getDate() != null && getText() != null && getUserId() != -1 &&
                getGameId() != -1){
            return true;
        }
        return false;
    }

    public String getShortenedText() {
        if(text.length() > 146){
            return text.substring(0,146) + " ...";
        }
        return text;
    }
}
