package org.pilirion.models.game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Ratings {
    private Connection db;

    public Ratings(Connection db){
        this.db = db;
    }

    public List<Rating> getAllRatingsForGame(int gameId){
        String sql = "select * from rating where game_id = "+String.valueOf(gameId);
        return getFromDb(sql);
    }

    public List<Rating> getFromDb(String sql){
        List<Rating> ratings = new ArrayList<Rating>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsRatings = stmt.executeQuery(sql);
            Rating rating;
            int value, id, authorId, gameId;
            while(rsRatings.next()){
                value = rsRatings.getInt("rating");
                id = rsRatings.getInt("id");
                authorId = rsRatings.getInt("user_id");
                gameId = rsRatings.getInt("game_id");
                rating = new Rating(id, value, authorId, gameId);
                ratings.add(rating);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return ratings;
    }

    public Rating getRatingGameUser(int gameId, int userId){
        String sql = "select * from rating where game_id = "+String.valueOf(gameId)+" and user_id ="+
                String.valueOf(userId);
        List<Rating> ratings = getFromDb(sql);
        if(ratings != null && ratings.size() > 0){
            return ratings.get(0);
        }
        return null;
    }

    public boolean insertRating(Rating toAdd){
        if(toAdd.validate()){
            String sql = "insert into rating (game_id, user_id, rating) values("+toAdd.getGameId()+","+toAdd.getAuthorId()+"," +
                    toAdd.getRating()+")";
            try{
                Statement stmt = db.createStatement();
                stmt.execute(sql);
                return true;
            } catch (SQLException ex){
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public List<Rating> getRatings() {
        String sql = "select * from rating";
        return getFromDb(sql);
    }

    public int getAverage() {
        List<Rating> ratings = getRatings();
        int amountOfRatings = 0;
        int wholeRating = 0;
        for(Rating rating: ratings){
            wholeRating += rating.getRating();
            amountOfRatings++;
        }
        wholeRating *= 10;
        if(amountOfRatings > 0){
            return wholeRating / amountOfRatings;
        } else {
            return 0;
        }
    }

    public void removeRating(int id) {
        String sql = "delete from rating where id = " + id;
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
