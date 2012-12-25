package org.pilirion.models.user;

import org.pilirion.models.game.Author;
import org.pilirion.models.game.Authors;
import org.pilirion.models.game.Games;
import org.pilirion.utils.Strings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Groups {
    private Connection db;

    public Groups(Connection db) {
        this.db = db;
    }

    public List<Group> getFromDb(String sql) {
        List<Group> groups = new ArrayList<Group>();
        try {
            Statement stmt = db.createStatement();
            ResultSet rsGames = stmt.executeQuery(sql);
            String name, image;
            int id;
            Authors authors = new Authors(db);

            Group group;
            while (rsGames.next()) {
                id = rsGames.getInt("id");
                name = rsGames.getString("name");
                image = rsGames.getString("image");

                group = new Group(id, name, image, authors.getGroupAuthors(id));
                groups.add(group);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return groups;
    }

    public List<Group> getAuthorsGroups(int authorId){
        String sql = "select * from csld_group where id in (select group_id from author_has_group where " +
                "author_id = "+authorId+")";
        return getFromDb(sql);
    }

    public Group getGroupWithBiggestProduction(){
        String groupSql = "select * from csld_group";
        List<Group> groups = getFromDb(groupSql);
        List<Author> authors;
        Games games = new Games(db);
        Group bestGroup = null;
        int amountOfProduction = 0;
        int maxProduction = 0;
        for(Group group: groups){
            authors = group.getAuthors();
            for(Author author: authors){
                amountOfProduction += games.getGamesOfAuthor(author.getId()).size();
            }
            if(maxProduction < amountOfProduction){
                maxProduction = amountOfProduction;
                bestGroup = group;
            }
        }
        return bestGroup;
    }

    public Group getById(int groupId) {
        String sql = "select * from csld_group where id = " + groupId;
        List<Group> groups = getFromDb(sql);
        if(groups.size() > 0){
            return groups.get(0);
        } else {
            return null;
        }
    }

    public String getNamesAsArray() {
        String sql = "select * from csld_group";
        List<Group> groups = getFromDb(sql);
        String arrayNames = "";
        for(Group group: groups){
            arrayNames += "'" + group.getName() + "',";
        }
        if(!arrayNames.equals("")){
            arrayNames = Strings.removeLast(arrayNames);
        }
        return arrayNames;
    }

    public int insert(Group group) {
        String sql = "insert into csld_group (id, name, image) values (DEFAULT,'"+group.getName()+"','"+group.getImage()+"') " +
                "RETURNING id";
        try {
            Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int id = -1;
            while(rs.next()){
                id = rs.getInt("id");
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public Group getGroupByName(String authorGroup) {
        String sql = "select * from csld_group where name = '" + authorGroup + "'";
        List<Group> groups = getFromDb(sql);
        if(groups.size() > 0){
            return groups.get(0);
        } else {
            return null;
        }
    }

    public List<Group> getGroups() {
        String sql = "select * from csld_group";
        return getFromDb(sql);  //To change body of created methods use File | Settings | File Templates.
    }
}