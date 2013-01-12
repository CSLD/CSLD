package org.pilirion.models.game;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 20:50
 */
public class Comments {
    private Connection db;

    public Comments(Connection db){
        this.db = db;
    }

    public List<Comment> getFromDb(String sql){
        List<Comment> comments = new ArrayList<Comment>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsComments = stmt.executeQuery(sql);
            Comment comment;
            String text;
            Date date;
            int id, userId, gameId;
            while(rsComments.next()){
                text = rsComments.getString("text");
                date = rsComments.getDate("date");
                id = rsComments.getInt("id");
                userId = rsComments.getInt("user_id");
                gameId = rsComments.getInt("game_id");
                comment = new Comment(id, date, text, userId, gameId);
                comments.add(comment);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return comments;
    }

    public List<Comment> getCommentsByGame(int gameId){
        String sql = "select * from comment where game_id = " + String.valueOf(gameId);
        return getFromDb(sql);
    }

    public List<Comment> getLastComments(int amount) {
        String sql = "select * from comment order by time desc limit " + String.valueOf(amount);
        return getFromDb(sql);
    }

    public List<Comment> getComments() {
        String sql = "select * from comment";
        return getFromDb(sql);
    }

    public List<Comment> getCommentsOfUser(int userId) {
        String sql = "select * from comment where user_id = " + String.valueOf(userId);
        return getFromDb(sql);
    }

    public boolean insertComment(Comment comment){
        if(comment.isValid()){
            String sql = "insert into comment (user_id, game_id, date, text) values ("+comment.getUserId()+"," +
                    ""+comment.getGameId()+", '"+comment.getDate()+"', '"+comment.getText()+"')";
            try{
                Statement stmt = db.createStatement();
                stmt.execute(sql);
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void editComment(Comment comment){
        if(comment.isValid()){
            try {
                PreparedStatement pstmt = db.prepareStatement("update comment set time=now(), text=? where " +
                        "user_id = ? and game_id = ?");
                pstmt.setString(1, comment.getText());
                pstmt.setInt(2, comment.getUserId());
                pstmt.setInt(3, comment.getGameId());
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Comment getCommentGameUser(int userId, int gameId) {
        String sql = "select * from comment where game_id = " + gameId +" and user_id = " + userId;
        List<Comment> comments = getFromDb(sql);
        if(comments.size() > 0){
            return comments.get(0);
        } else {
            return null;
        }
    }
}
