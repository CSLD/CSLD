package org.pilirion.models.game;

import org.pilirion.models.user.Person;
import org.pilirion.models.user.Persons;
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
 * Time: 13:07
 */
public class Authors {
    private Connection db;

    public Authors(Connection db){
        this.db = db;
    }

    public List<Author> getAuthorsOfGame(int gameId){
        String sql = "select * from author where author_id in (select author_id from game_has_authors where " +
                "game_id = "+String.valueOf(gameId)+")";
        return getAuthorsFromDB(sql);
    }

    List<Author> getAuthorsFromDB(String sql){
        List<Author> authors= new ArrayList<Author>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsAuthors = stmt.executeQuery(sql);
            Author author;
            int authorId, personId;
            Person person;
            Persons persons = new Persons(db);
            while(rsAuthors.next()){
                authorId = rsAuthors.getInt("author_id");
                personId = rsAuthors.getInt("person_id");
                person = persons.getPersonById(personId);
                author = new Author(authorId, person);
                authors.add(author);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }

        return authors;
    }

    public List<Author> getAllAuthors(){
        String sql = "select * from author";
        return getAuthorsFromDB(sql);
    }

    public Author getBestAuthor(){
        //Best author is the author with best game. First one from list.
        Games games = new Games(db);
        List<Game> bestGames = games.getGamesOrderByRating(1,1);
        try{
            Game bestGame = bestGames.get(0);
            return bestGame.getAuthors().get(0);
        } catch(IndexOutOfBoundsException ex){
            return null;
        } catch(NullPointerException ex){
            return null;
        }
    }

    public boolean editUser(int userId, int authorId){
        String sql = "update author set person_id = " + userId + " where author_id = " + authorId;
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Author> getAllAuthors(int actualPage, int authorsPerPage){
        int offset = (actualPage - 1) * authorsPerPage;
        String sql = "select * from author offset "+offset+" limit "+authorsPerPage;
        return getAuthorsFromDB(sql);
    }

    public int insertAuthor(Author author){
        int personId = (author.getPerson() != null ) ? author.getPerson().getId() : -1;

        if(personId == -1){
            if(author.getPerson() != null){
                Persons persons = new Persons(db);
                personId = persons.insertPerson(author.getPerson());
                if(personId == -1){
                    return -1;
                }
            } else {
                return -1;
            }
        }
        String sql = "insert into author (author_id, person_id) values (DEFAULT,"+personId+") RETURNING author_id";
        try{
            Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                return rs.getInt("author_id");
            }
        } catch(SQLException ex){
            return -1;
        }
        return -1;
    }

    public boolean insertAuthorToGroup(int authorId, int groupId){
        String sql = "insert into author_has_group(author_id, group_id) values("+authorId+","+groupId+")";
        try {
            Statement stmt = db.createStatement();
            return stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Author getById(int id){
        String sql = "select * from author where author_id = " + id;
        List<Author> authors = getAuthorsFromDB(sql);
        if(authors != null && authors.size() > 0){
            return authors.get(0);
        }
        return null;
    }

    public Author getByPersonId(int personId) {
        String sql = "select * from author where person_id = " + personId;
        List<Author> authors = getAuthorsFromDB(sql);
        if(authors != null && authors.size() > 0){
            return authors.get(0);
        }
        return null;
    }

    public String getNamesAsArray() {
        List<Author> allAuthors = getAllAuthors();
        String arrayNames = "";
        for(Author author: allAuthors){
            arrayNames += "'" + author.getPerson().getNameDiff() + "',";
        }
        if(!arrayNames.equals("")){
            arrayNames = Strings.removeLast(arrayNames);
        }
        return arrayNames;
    }

    public List<Author> getAuthorsByName(String firstName, String nickName, String lastName) {
        nickName = nickName.replaceAll("\"","");
        String sql = "select * from author where person_id in (select id from person where " +
                "first_name='"+firstName+"' and last_name='"+lastName+"' and nickname ilike'%"+nickName+"%')";
        return getAuthorsFromDB(sql);
    }

    public List<Author> getAuthorsByNames(List<String> authors) {
        List<Author> allAuthors = new ArrayList<Author>();
        String firstName, nickName, lastName = "";
        String[] nameParts;
        for(String author: authors){
            lastName = "";
            nameParts = author.trim().replaceAll(" +", " ").split(" ");
            if(nameParts.length == 4 ){
                firstName = nameParts[0];
                if(nameParts[1].indexOf("\"") != -1){
                    nickName = nameParts[1].replaceAll("\"","");
                    lastName = nameParts[2];
                } else {
                    firstName += " " + nameParts[1];
                    nickName = nameParts[2].replaceAll("\"","");
                }
                lastName += nameParts[3];
            } else if(nameParts.length == 3 ){
                firstName = nameParts[0];
                if(nameParts[1].indexOf("\"") != -1){
                    nickName = nameParts[1].replaceAll("\"","");
                } else {
                    firstName += " " + nameParts[1];
                    nickName = "";
                }
                lastName = nameParts[2];
            } else {
                firstName = nameParts[0];
                nickName = "";
                lastName = nameParts[1];
            }
            System.out.println(firstName + " " + nickName + " " + lastName);
            allAuthors.addAll(getAuthorsByName(firstName, nickName, lastName));
        }
        return allAuthors;
    }

    public List<Author> getGroupAuthors(int groupId) {
        String sql = "select * from author where author_id in (select author_id from author_has_group where " +
                "group_id = "+groupId+")";
        return getAuthorsFromDB(sql);
    }
}
