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
 * Date: 25.6.12
 * Time: 21:29
 */
public class Roles {
    private String dbTable = "role";
    private Connection db;

    public Roles(Connection db){
        this.db = db;
    }

    public List<Role> getAllRoles(){
        String sql = "select * from "+dbTable;
        return getRolesDb(sql);
    }

    public List<Role> getRolesDb(String sql){
        List<Role> roles = new ArrayList<Role>();
        try {
            Statement stmt = db.createStatement();
            ResultSet rsRoles = stmt.executeQuery(sql);
            Role role;
            int id;
            String name;
            while(rsRoles.next()){
                id = rsRoles.getInt("id");
                name = rsRoles.getString("name");
                role = new Role(id, name);
                roles.add(role);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return roles;
    }

    public Role getById(int roleId){
        String sql = "select * from "+dbTable+" where id = " + String.valueOf(roleId);
        List<Role> roles = getRolesDb(sql);
        if(roles.size() > 0){
            return roles.get(0);
        }
        return null;
    }
}
