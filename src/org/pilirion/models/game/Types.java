package org.pilirion.models.game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 13:55
 */
public class Types {
    private Connection db;

    public Types(Connection db){
        this.db = db;
    }

    public List<Type> getAllTypes(){
        String sql = "select * from type";
        return getTypesFromDb(sql);
    }

    public List<Type> getTypesFromDb(String sql){
        List<Type> types = new ArrayList<Type>();
        Type type;
        try{
            Statement stmt = db.createStatement();
            ResultSet rsTypes = stmt.executeQuery(sql);
            String name;
            int id;
            while(rsTypes.next()){
                id = rsTypes.getInt("id");
                name = rsTypes.getString("name");
                type = new Type(id, name);
                types.add(type);
            }
        } catch (SQLException ex){
            // TODO log
            return null;
        }
        return types;
    }

    public Type getById(int id){
        String sql = "select * from type where id = "+String.valueOf(id);
        List<Type> types = getTypesFromDb(sql);
        if(types != null && types.size() > 0){
            return types.get(0);
        }
        return null;
    }

    public boolean insertType(Type typeToAdd){
        if(typeToAdd.getName() != null){
            String sql = "insert into type (name) values ('"+typeToAdd.getName()+"')";
            try{
                Statement stmt = db.createStatement();
                stmt.execute(sql);
                return true;
            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
    }
}
