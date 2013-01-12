package org.pilirion.models.user;

import org.pilirion.utils.Strings;

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
 * Time: 13:42
 */
public class Users {
    private Connection db;
    private String tableName = "csld_user";

    public  Users(Connection db){
        this.db = db;
    }

    public List<User> getAllUsers(){
        String sql = "select * from "+tableName+" users, person where person.id = users.id";
        return getUsersSql(sql);
    }

    private List<User> getUsersSql(String sql){
        List<User> users = new ArrayList<User>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsUsers = stmt.executeQuery(sql);
            User user;
            String userName, password;
            int id, roleId;
            Roles roles = new Roles(db);
            Persons persons = new Persons(db);
            Person person;
            Role role;
            while(rsUsers.next()){
                id = rsUsers.getInt("id");
                roleId = rsUsers.getInt("role_id");
                role = roles.getById(roleId);
                person = persons.getPersonById(id);
                userName = rsUsers.getString("user_name");
                password = rsUsers.getString("password");

                user = new User(id, person, userName, password, role);
                users.add(user);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return users;
    }

    public User getById(int id){
        String sql = "select * from "+tableName+" users, person where person.id = users.id and " +
                "users.id = " + String.valueOf(id);
        List<User> users = getUsersSql(sql);
        if(users != null && users.size() > 0){
            return users.get(0);
        }
        return null;
    }

    public boolean insertUser(User userToAdd){
        if(userToAdd.validate()){
            Persons persons = new Persons(db);
            int id = persons.insertPerson(userToAdd.getPerson());
            if(id == -1){
                return false;
            }
            try{
                String userRole = "2";
                Statement stmt = db.createStatement();
                String sql = "insert into csld_user (id, user_name, password, role_id) values ("+id+",'"+
                        userToAdd.getUserName()+"','"+userToAdd.getPassword()+"', '"+userRole+"')";
                stmt.execute(sql);
            } catch(SQLException ex){
                return false;
            }
        }
        return true;
    }

    public boolean editUser(User toEdit){
        // TODO update in transaction
        if(toEdit.getId() != -1){
            Persons persons = new Persons(db);
            if(toEdit.getPerson() != null){
                System.out.println("GETPerson");
                persons.editPerson(toEdit.getPerson());
            }
            String sql = "update csld_user set  ";
            if(toEdit.getUserName() != null && !toEdit.getUserName().equals("")){
                sql += "user_name='"+toEdit.getUserName()+"',";
            }
            if(toEdit.getPassword() != null && !toEdit.getPassword().equals("")){
                sql += "password='"+toEdit.getPassword()+"',";
            }
            sql = Strings.removeLast(sql);
            sql += " where id = "+toEdit.getId();
            try{
                Statement stmt = db.createStatement();
                stmt.execute(sql);
                return true;
            } catch(SQLException ex){
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean isNameFree(String userName){
        String sql = "select * from csld_user where user_name ilike '"+userName+"'";
        List<User> foundUser = getUsersSql(sql);
        if(foundUser != null && foundUser.size() == 0){
            return true;
        }
        return false;
    }

    public User authenticate(String userName, String password){
        String sql = "select * from csld_user where user_name ilike '"+userName+"' and password='"+password+"'";
        List<User> foundUser = getUsersSql(sql);
        if(foundUser != null && foundUser.size() > 0){
            return foundUser.get(0);
        }
        return null;
    }

    public List<User> getUsersOrderedByComments(){
        String sql = "select * from csld_user order by " +
                "(select count(id) from comment where user_id=csld_user.id) desc";
        return getUsersSql(sql);
    }

    public List<User> getUsersOrderedByRating(int actualPage, int usersPerPage){
        int offset = (actualPage -1) * usersPerPage;
        String sql = "select first_name, last_name, nickname, birth_date, user_name, email, person.id as id, role_id, password, " +
                "csld_countUserRating(users.id) as rating from csld_user users, person where person.id = users.id order by " +
                "rating desc offset "+offset+" limit "+usersPerPage;
        return getUsersSql(sql);
    }

    public List<User> getAdministrators() {
        String sql = "select * from csld_user where role_id = 5";
        return getUsersSql(sql);
    }

    public List<User> getModerators() {
        String sql = "select * from csld_user where role_id = 4";
        return getUsersSql(sql);  //To change body of created methods use File | Settings | File Templates.
    }

    public User getLarpKing() {
        String sql = "select * from csld_user where id in ( select user_id from user_played_game group by user_id order by count(game_id) desc limit 1)";
        List<User> usersList = getUsersSql(sql);
        if(usersList != null && usersList.size() > 0){
            return usersList.get(0);
        }
        return null;
    }

    public User getKingCommenter() {
        List<User> byComment = getUsersOrderedByComments();
        if(byComment.size() > 0){
            return byComment.get(0);
        } else {
            return null;
        }
    }
}
