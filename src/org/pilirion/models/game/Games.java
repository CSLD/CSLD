package org.pilirion.models.game;

import org.pilirion.exceptions.ItemDoesNotExists;
import org.pilirion.models.user.Group;
import org.pilirion.models.user.Groups;
import org.pilirion.utils.Strings;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Games {
    private Connection db;
    public Games(Connection db){
        this.db = db;
    }

    public List<Game> getGames(){
        String sql = "select * from game order by id";
        return getFromDb(sql);
    }

    public List<Game> getFromDb(String sql){
        List<Game> games = new ArrayList<Game>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsGames = stmt.executeQuery(sql);
            String name, image, description, gameWeb;
            int id, year, menRoles, womenRoles, hours, days, players, bothRoles, userWhoAddGame;
            Date premier;
            Authors objAuthors = new Authors(db);
            Comments objComments = new Comments(db);
            Ratings objRatings = new Ratings(db);
            Labels objLabels = new Labels(db);

            List<Author> authors;
            List<Comment> comments;
            List<Rating> ratings;
            List<Label> labels;
            Game game;
            while(rsGames.next()){
                id = rsGames.getInt("id");
                year = rsGames.getInt("year");
                name = rsGames.getString("name");
                image = rsGames.getString("image");
                description = rsGames.getString("description");
                menRoles = rsGames.getInt("men_role");
                womenRoles = rsGames.getInt("women_role");
                hours = rsGames.getInt("hours");
                days = rsGames.getInt("days");
                players = rsGames.getInt("players");
                premier = rsGames.getDate("premier");
                bothRoles = rsGames.getInt("both_role");
                userWhoAddGame = rsGames.getInt("user_who_added");
                gameWeb = rsGames.getString("web");

                authors = objAuthors.getAuthorsOfGame(id);
                comments = objComments.getCommentsByGame(id);
                ratings = objRatings.getAllRatingsForGame(id);
                labels = objLabels.getLabelsForGame(id);

                game = new Game(id, name, image, menRoles, womenRoles, bothRoles, hours, days, year, description, premier, players, gameWeb,
                    authors, comments, ratings, labels);
                game.setUserWhoAddedGame(userWhoAddGame);
                games.add(game);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return games;
    }

    public Game getById(int gameId){
        String sql = "select * from game where id = " + String.valueOf(gameId);
        List<Game> games = getFromDb(sql);
        if(games != null && games.size() > 0){
            return games.get(0);
        }
        return null;
    }

    public Game getByName(String name) throws ItemDoesNotExists {
        String sql = "select * from game where name ilike '" + name + "'";
        List<Game> games = getFromDb(sql);
        if(games != null && games.size() > 0){
            return games.get(0);
        } else {
            throw new ItemDoesNotExists();
        }
    }

    public boolean insertGame(Game game){
        if(game.isValid()){
            String premier = (game.getPremier() != null) ? "'" + game.getPremier().toString() + "'" : null;

            String sql = "insert into game (id, name, year, description, image, men_role, women_role, both_role, hours, days, players, " +
                    "premier, user_who_added, web) values (DEFAULT, '"+game.getName()+"',"+String.valueOf(game.getYear())+",'"+game.getDescription()+"'," +
                    "'"+game.getImage()+"',"+String.valueOf(game.getMenRoles())+","+String.valueOf(game.getWomenRoles())+","+String.valueOf(game.getBothRole())+"," +
                    ""+String.valueOf(game.getHours())+","+String.valueOf(game.getDays())+","+String.valueOf(game.getPlayers())+"," +
                    ""+premier+","+game.getUserWhoAddedGame()+",'"+game.getWeb()+"') " +
                    "returning id";
            try{
                Statement stmt = db.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                int id = -1;
                while(rs.next()){
                    id = rs.getInt("id");
                }
                List<Author> authors = game.getAuthors();
                if(authors != null && id != -1){
                    String authorSql;
                    for(Author author: authors){
                        authorSql = "insert into game_has_authors (game_id, author_id) values ("+String.valueOf(id)+","+
                                String.valueOf(author.getId())+")";
                        stmt.execute(authorSql);
                    }
                }
                List<Label> labels = game.getLabels();
                if(labels != null && id != -1){
                    String labelSql;
                    for(Label label: labels) {
                        labelSql = "insert into game_has_labels (id, game_id, label_id) values (DEFAULT,"+String.valueOf(id)+"," +
                                ""+String.valueOf(label.getId())+")";
                        stmt.execute(labelSql);
                    }
                }
                return true;
            } catch (SQLException ex){
                return false;
            }
        }
        return false;
    }

    public List<Game> getGamesOfAuthor(int authorId){
        String sql = "select * from game where id in (select game_id from game_has_authors where author_id = "+
                authorId+")";
        return getFromDb(sql);
    }

    public Game getBestGameOfAuthor(int authorId){
        String sql = "select id, csld_countRating(id)as ratings, year, name, image, description, " +
                "men_role, women_role, both_role, user_who_added, hours, days, players, premier, web from game where " +
                "id in (select game_id from game_has_authors where author_id = " + authorId + ") " +
                "order by ratings desc limit 1";
        List<Game> games = getFromDb(sql);
        if(games.size() > 0){
            return games.get(0);
        } else {
            return null;
        }
    }

    public List<Game> getGamesByGroup(int groupId){
        Groups groups = new Groups(db);
        Group group = groups.getById(groupId);
        return getGamesOfAuthors(group.getAuthors());
    }

    public List<Game> getGames(int iActualPage, int iGamesPerPage) {
        int iOffset = (iActualPage - 1) * iGamesPerPage;
        String sSql = "select * from game order by id desc limit "+String.valueOf(iGamesPerPage) + " offset " +
                String.valueOf(iOffset);
        return getFromDb(sSql);
    }

    public List<Game> getGamesOrderByRating(int iActualPage, int iGamesPerPage) {
        int iOffset = (iActualPage - 1) * iGamesPerPage;
        String sSql = "select id, csld_countRating(id)as ratings, year, name, image, description, " +
                "men_role, women_role, both_role, user_who_added, hours, days, players, premier, web from game " +
                "order by ratings desc " +
                "limit "+String.valueOf(iGamesPerPage) + " offset " + String.valueOf(iOffset);
        return getFromDb(sSql);
    }

    public List<Game> getGamesMostCommented(int iActualPage, int iGamesPerPage) {
        int iOffset = (iActualPage - 1) * iGamesPerPage;
        String sSql = "select id, csld_countComments(id)as comments, year, name, image, description, " +
                "men_role, women_role, both_role, user_who_added, hours, days, players, premier, web from game " +
                "order by comments " +
                "limit "+String.valueOf(iGamesPerPage) + " offset " + String.valueOf(iOffset);
        return getFromDb(sSql);
    }

    public boolean isUserPlayerOfGame(int userId, int gameId) {
        String sql = "select * from user_played_game where user_id = " + userId + " and game_id = " + gameId;
        try {
            Statement stmt = db.createStatement();
            ResultSet usersPlayGame = stmt.executeQuery(sql);
            while(usersPlayGame.next()){
                return true;
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public void setUserAsPlayerOfGame(int userId, int gameId) {
        String sql = "insert into user_played_game values ("+userId+","+gameId+")";
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void removeUserAsPlayerOfGame(int userId, int gameId) {
        String sql = "delete from user_played_game where user_id = "+userId+" and game_id = "+gameId;
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public List<Game> getAttendedGames(int userId, int actualPage, int gamesPerPage) {
        int offset = (actualPage - 1) * gamesPerPage;
        String sql = "select * from game where id in (select game_id from user_played_game where " +
                "user_id = "+userId+") order by id desc limit "+String.valueOf(gamesPerPage) + " " +
                "offset " + String.valueOf(offset);
        return getFromDb(sql);
    }

    public List<Game> getCommentedGames(int userId, int actualPage, int gamesPerPage) {
        int offset = (actualPage - 1) * gamesPerPage;
        String sql = "select * from game where id in (select game_id from comment where " +
                "user_id = "+userId+") order by id desc limit "+String.valueOf(gamesPerPage) + " " +
                "offset " + String.valueOf(offset);
        return getFromDb(sql);
    }

    public List<Game> getRatedGames(int userId, int actualPage, int gamesPerPage) {
        int offset = (actualPage - 1) * gamesPerPage;
        String sql = "select * from game where id in (select game_id from rating where " +
                "user_id = "+userId+") order by id desc limit "+String.valueOf(gamesPerPage) + " " +
                "offset " + String.valueOf(offset);
        return getFromDb(sql);
    }

    public List<Game> getAttendedGames(int userId) {
        String sql = "select * from game where id in (select game_id from user_played_game where " +
                "user_id = "+userId+") order by id desc";
        return getFromDb(sql);
    }

    public List<Game> getCommentedGames(int userId) {
        String sql = "select * from game where id in (select game_id from comment where " +
                "user_id = "+userId+") order by id desc";
        return getFromDb(sql);
    }

    public List<Game> getRatedGames(int userId) {
        String sql = "select * from game where id in (select game_id from rating where " +
                "user_id = "+userId+") order by id desc";
        return getFromDb(sql);
    }

    public List<Game> getLastAdded(int amount) {
        String sql = "select * from game order by added desc limit " + String.valueOf(amount);
        return getFromDb(sql);
    }

    public List<Game> getLastAdded() {
        String sql = "select * from game order by added desc";
        return getFromDb(sql);
    }

    public Game getRandomGame() {
        String sql = "select * from game";
        List<Game> allGames = getFromDb(sql);
        if(allGames.size() > 0){
            int chosenGame = (int) Math.round(Math.random() * (double)allGames.size());
            return allGames.get((chosenGame - 1 < 0) ? 0 : chosenGame - 1);
        }
        return null;
    }

    public List<Game> getGamesOfAuthors(List<Author> authors){
        String authorsIds = "";
        for(Author author: authors){
            authorsIds += author.getId() + ",";
        }
        if(!authorsIds.equals("")){
            authorsIds = Strings.removeLast(authorsIds);
            String sql = "select * from game where id in (select game_id from game_has_authors where ARRAY[author_id] <@ ARRAY[" +
                    authorsIds + "])";
            return getFromDb(sql);
        } else {
            return new ArrayList<Game>();
        }
    }

    public List<Game> getSimilarLarps(Game game, int amountOfSimilar){
        List<Label> labels = game.getLabels();
        String sql = "select * from game where id in (select game_id from game_has_labels where ARRAY[label_id] <@ ARRAY[";
        for(Label label: labels){
            sql += label.getId() + ",";
        }
        if(labels.size() > 0){
            sql = sql.substring(0, sql.length() - 1);
        }
        sql += "]) limit " + amountOfSimilar;
        return getFromDb(sql);
    }

    public boolean editGame(Game game) {
        if(game.getId() == -1){
            return false;
        }

        try{
            PreparedStatement pstmt = db.prepareStatement("update game set name = ?, year = ?, description = ?," +
                    "image = ?, men_role = ?, women_role = ?, hours = ?, days = ?, players = ?, both_role = ?, " +
                    "web = ? where id = ?");
            pstmt.setString(1, game.getName());
            pstmt.setInt(2, game.getYear());
            pstmt.setString(3, game.getDescription());
            pstmt.setString(4, game.getImage());
            pstmt.setInt(5, game.getMenRoles());
            pstmt.setInt(6, game.getWomenRoles());
            pstmt.setInt(7, game.getHours());
            pstmt.setInt(8, game.getDays());
            pstmt.setInt(9, game.getPlayersAmount());
            pstmt.setInt(10, game.getBothRole());
            pstmt.setString(11, game.getWeb());
            pstmt.setInt(12, game.getId());
            pstmt.execute();

            Statement stmt = db.createStatement();
            List<Author> authors = game.getAuthors();
            if(authors != null){
                stmt.execute("delete from game_has_authors where game_id = " + game.getId());
                String authorSql;
                for(Author author: authors){
                    authorSql = "insert into game_has_authors (game_id, author_id) values ("+String.valueOf(game.getId())+","+
                            String.valueOf(author.getId())+")";
                    stmt.execute(authorSql);
                }
            }
            List<Label> labels = game.getLabels();
            if(labels != null){
                stmt.execute("delete from game_has_labels where game_id = " + game.getId());
                String labelSql;
                for(Label label: labels) {
                    labelSql = "insert into game_has_labels (id, game_id, label_id) values (DEFAULT,"+String.valueOf(game.getId())+"," +
                            ""+String.valueOf(label.getId())+")";
                    stmt.execute(labelSql);
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
