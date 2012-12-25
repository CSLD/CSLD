package org.pilirion.models.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 5.7.12
 * Time: 20:14
 */
public class Ratings {
    private Connection db;

    public Ratings(Connection db){
        this.db = db;
    }

    public List<Rating> getAllRatingsGroupedByUsers(){
        String sql = "select * from user_rating group by user_id";
        return getRatingsDb(sql);
    }

    public List<Rating> getRatingsDb(String sql){
        List<Rating> ratings = new ArrayList<Rating>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsRatings = stmt.executeQuery(sql);
            Rating rating;
            int ratingValue, id, userId;
            String description;
            while(rsRatings.next()){
                ratingValue = rsRatings.getInt("rating");
                id = rsRatings.getInt("id");
                userId = rsRatings.getInt("user_id");
                description = rsRatings.getString("description");
                rating = new Rating(id, userId, ratingValue, description);
                ratings.add(rating);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return ratings;
    }

    public List<Rating> getAllRatingsForUser(int userId, int actualPage, int ratingsPerPage){
        int offset = (actualPage - 1) * ratingsPerPage;
        String sql = "select * from user_rating where user_id = " + userId + " offset " + offset + " limit "+
                ratingsPerPage;
        return getRatingsDb(sql);
    }

    public List<Rating> getAllRatingsForUser(int userId){
        String sql = "select * from user_rating where user_id = " + userId;
        return getRatingsDb(sql);
    }

    public Rating getRatingForUser(int userId){
        String sql = "select -1 as id, '' as description, user_id, sum(rating) as rating from user_rating where user_id = "+String.valueOf(userId)+
            " group by user_id";
        List<Rating> ratings =getRatingsDb(sql);
        if(ratings != null && ratings.size() > 0){
            return ratings.get(0);
        }
        return new Rating(-1,userId,0,"");
    }

    public boolean insertRating(Rating toAdd){
        if(toAdd.validate()){
            String sql = "insert into user_rating (user_id, rating, description) values("+toAdd.getUserId()+"," +
                    toAdd.getRating()+",'"+toAdd.getDescription()+"')";
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

    public Rating getByType(String type, int userId) {
        if(type == "Comment"){
            int value = 2;
            String description = "You commented game";
            return new Rating(-1,userId, value, description);
        } else if(type == "Rating"){
            int value = 1;
            String description = "You rated game";
            return new Rating(-1,userId, value, description);
        } else if (type == "Insert") {
            int value = 4;
            String description = "You inserted game";
            return new Rating(-1,userId, value, description);
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}