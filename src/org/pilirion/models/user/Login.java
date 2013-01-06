package org.pilirion.models.user;

import org.pilirion.exceptions.WrongCookie;
import org.pilirion.utils.Pwd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 6.1.13
 * Time: 9:41
 */
public class Login {
    private Connection db;
    private Users users;

    public Login(Connection db){
        this.db = db;
        this.users = new Users(db);
    }

    public String getCookieVal(int userId, String userName){
        UUID userNumber = UUID.randomUUID();
        String result = Pwd.getMD5(userNumber.toString() + userId + userName);
        return result;
    }

    public void storeCookie(String userId, String value){
        String sql = "insert into cookies_stored values ("+userId+",'"+value+"')";
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void invalidateCookie(String userId, String value){
        String sql = "delete from cookies_stored where user_id = " + userId + " and " +
                "cookieValue = '"+value+"'";
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByCookie(String cookieValue) throws WrongCookie{
        String sql = "select user_id from cookies_stored where cookieValue = '" + cookieValue + "'";
        try {
            Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int userId = -1;
            while(rs.next()){
                userId = rs.getInt(1);
            }
            if(userId != -1){
                return users.getById(userId);
            } else {
                throw new WrongCookie();
            }
        } catch (SQLException e) {
            throw new WrongCookie();
        }
    }
}
