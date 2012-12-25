package org.pilirion.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 20.6.12
 * Time: 16:47
 */
public class DbConnection {
    public static Connection create(String url, String user, String password){
        try {
            //String url = "jdbc:postgresql://localhost/test";
            Class.forName("org.postgresql.Driver");
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            Connection conn = DriverManager.getConnection(url, props);
            return conn;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
