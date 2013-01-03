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
 * Date: 4.11.12
 * Time: 10:56
 */
public class Search {
    private Connection db;
    private Games games;
    private Authors authors;

    public Search(Connection db){
        this.db = db;
        games = new Games(db);
        authors = new Authors(db);
    }

    public List<Game> getByAll(String query){
        String sql = "select * from (((game join game_has_authors on game.id = game_has_authors.game_id)" +
                " join author on author.author_id = game_has_authors.author_id) join person on person.id = author.author_id)" +
                " where (person.first_name || ' ' || person.last_name || ' ' || person.nickname) ilike '%" + query + "%' " +
                "or game.description like '%"+query+"%'";
        return games.getFromDb(sql);
    }

    public List<Game> getByAuthor(String authorQuery){
        String sql = "select * from (((game join game_has_authors on game.id = game_has_authors.game_id)" +
                " join author on author.author_id = game_has_authors.author_id) join person on person.id = author.author_id)" +
                " where (person.first_name || ' ' || person.last_name || ' ' || person.nickname) ilike '%" + authorQuery + "%'";
        return games.getFromDb(sql);
    }

    public List<Author> getAuthorByText(String authorQuery){
        String sql = "select * from author where person_id in ( select id from person where " +
                "(person.first_name || ' ' || person.last_name || ' ' || person.nickname) ilike '%" + authorQuery + "%')";
        return authors.getAuthorsFromDB(sql);
    }

    public List<Game> getByDescription(String descriptionQuery){
        String sql = "select * from game where description like '%"+descriptionQuery+"%'";
        return games.getFromDb(sql);
    }

    public List<Game> getByGameName(String nameQuery){
        String sql = "select * from game where name like '%"+nameQuery+"%'";
        return games.getFromDb(sql);
    }

    public List<Game> findGamesByParams(String fullText, List<String> usedLabels, String minPlayers,
                                        String maxPlayers, String minHours, String maxHours, String minDays,
                                        String maxDays) {
        List<Game> findGames = findByParams(fullText, usedLabels,minPlayers, maxPlayers, minHours, maxHours, minDays, maxDays, false);

        for(Game game: findGames){
            String sql = "select csld_countRating("+game.getId()+") from game where id = " + game.getId();
            try {
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    game.setRatingZebricek(rs.getDouble(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return findGames;
    }

    public List<Game> findGamesByParamsOrderByRating(String fullText, List<String> usedLabels, String minPlayers,
                                                     String maxPlayers, String minHours, String maxHours, String minDays, String maxDays) {
        List<Game> findGames = findByParams(fullText, usedLabels,minPlayers, maxPlayers, minHours, maxHours, minDays, maxDays, true);

        for(Game game: findGames){
            String sql = "select csld_countRating("+game.getId()+") from game where id = " + game.getId();
            try {
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    game.setRatingZebricek(rs.getDouble(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return findGames;
    }

    private List<Game> findByParams(String fullText, List<String> usedLabels, String minPlayers, String maxPlayers,
                                    String minHours, String maxHours, String minDays, String maxDays, boolean isOrdered){
        String sql = "select * from game";
        if((minPlayers != null && !minPlayers.equals("")) ||
                (maxPlayers != null && !maxPlayers.equals("")) ||
                (minHours != null && !minHours.equals("")) ||
                (maxHours != null && !maxHours.equals("")) ||
                (minDays != null && !minDays.equals("")) ||
                (maxDays != null && !maxDays.equals(""))){
            sql += " where ";
        }

        if(minPlayers != null && !minPlayers.equals("")){
            sql += " players >= " + minPlayers + " and ";
        }
        if(maxPlayers != null && !maxPlayers.equals("")){
            sql += " players <= " + maxPlayers + " and ";
        }
        if(minHours != null && !minHours.equals("")){
            sql += " hours >= " + minHours + " and ";
        }
        if(maxHours != null && !maxHours.equals("")){
            sql += " hours <= " + maxHours + " and ";
        }
        if(minDays != null && !minDays.equals("")){
            sql += " days >= " + minDays + " and ";
        }
        if(maxDays != null && !maxDays.equals("")) {
            sql += "days <= " + maxDays + " and ";
        }
        if(sql.endsWith("and ")){
            sql = sql.substring(0, sql.length() - 4);
        }
        if(isOrdered){
            sql += " order by csld_countRating(id) desc";
        }
        List<Game> allGames = games.getFromDb(sql);

        List<Game> result = new ArrayList<Game>();
        boolean add;
        for(Game game: allGames){
            add = true;
            if(fullText != null && !fullText.equals("")){
                if(!game.getName().toLowerCase().contains(fullText.toLowerCase())){
                    add = false;
                }
            }
            if(usedLabels != null && usedLabels.size() > 0){
                if(!game.hasLabels(usedLabels)){
                    add = false;
                }
            }

            if(add){
                result.add(game);
            }
        }
        return result;
    }
}
