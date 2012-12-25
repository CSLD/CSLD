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
 * Time: 21:32
 */
public class Resources {
    private String dbTable = "resource";
    private Connection db;

    public Resources(Connection db){
        this.db = db;
    }

    public List<Resource> getAllResources(){
        String sql = "select * from " + dbTable;
        return getResourcesFromDb(sql);
    }

    public List<Resource> getResourcesFromDb(String sql) {
        List<Resource> resources = new ArrayList<Resource>();
        try {
            Statement stmt = db.createStatement();
            ResultSet rsResources = stmt.executeQuery(sql);
            Resource resource;
            int id;
            String path, aDefault;
            while(rsResources.next()){
                id = rsResources.getInt("id");
                path = rsResources.getString("path");
                aDefault = rsResources.getString("default_path");
                resource = new Resource(id, path, aDefault);
                resources.add(resource);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return resources;
    }

    public Resource getResourceById(int resId){
        String sql = "select * from "+dbTable+" where id = " + String.valueOf(resId);
        List<Resource> resources = getResourcesFromDb(sql);
        if(resources.size() > 0){
            return resources.get(0);
        }
        return null;
    }

    public Resource getResourceByPath(String path){
        String sql = "select * from "+dbTable+" where path = '" + path + "'";
        List<Resource> resources = getResourcesFromDb(sql);
        if(resources.size() > 0){
            return resources.get(0);
        }
        return null;
    }

    public List<Resource> getResourceForRole(int roleId){
        String sql = "select * from "+dbTable+" where id in (select resource_id from role_has_resource where role_id = "+
                String.valueOf(roleId)+")";
        return getResourcesFromDb(sql);
    }
}
