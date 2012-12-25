package org.pilirion.models.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.6.12
 * Time: 16:30
 */
public class Settings {
    private Connection db;
    private int userId;
    private String dbTable = "settings";

    public Settings(Connection db, int userId){
        this.db = db;
        this.userId = userId;
    }

    public int getGamesPerPage(){
        int games = 5;
        String sql = "select fvaluenumr from settings where user_id = " + String.valueOf(userId) + " and name='" +
                "games_per_page'";
        try {
            Statement stmt = db.createStatement();
            ResultSet rsSetting = stmt.executeQuery(sql);
            while(rsSetting.next()){
                games = rsSetting.getInt(1);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return games;
    }

    public boolean existsGamesPerPage(){
        String sql = "select fvaluenumr from settings where user_id = " + String.valueOf(userId) + " and name='" +
                "games_per_page'";
        try {
            Statement stmt = db.createStatement();
            ResultSet rsSetting = stmt.executeQuery(sql);
            while(rsSetting.next()){
                return true;
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean setGamesPerPage(int gamesPerPage){
        String sql;
        if(existsGamesPerPage()){
            sql = "update settings set fvaluenumr = "+String.valueOf(gamesPerPage)+" where user_id = "+
                String.valueOf(userId) + " and name= 'games_per_page'";
        } else {
            sql = "insert into settings (user_id, name, fvaluenumr) values ("+userId+",'games_per_page',"+gamesPerPage+")";
        }
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
