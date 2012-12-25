package org.pilirion.models.game;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Labels {
    private Connection db;
    public Labels(Connection db){
        this.db = db;
    }

    public List<Label> getLabelsFromDb(String sql){
        List<Label> labels = new ArrayList<Label>();
        try{
            Statement stmt = db.createStatement();
            ResultSet rsLabels = stmt.executeQuery(sql);
            String name, description;
            int id, userId;
            boolean requires, isAuthorized;

            Label label;
            while(rsLabels.next()){
                name = rsLabels.getString("name");
                description = rsLabels.getString("description");
                id = rsLabels.getInt("id");
                requires = rsLabels.getBoolean("requires");
                isAuthorized = rsLabels.getBoolean("is_authorized");
                userId = rsLabels.getInt("user_id");

                label = new Label(id, name, description, requires, isAuthorized, userId);
                labels.add(label);
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return labels;
    }

    public List<Label> getLabels(){
        String sql = "select * from label";
        return getLabelsFromDb(sql);
    }

    public List<Label> getRequiredLabels() {
        String sql = "select * from label where requires=TRUE";
        return getLabelsFromDb(sql);
    }

    public List<Label> getOtherLabels() {
        String sql = "select * from label where requires<>TRUE";
        return getLabelsFromDb(sql);
    }

    public List<Label> getLabelsForGame(int id) {
        String sql = "select * from label where id in (select label_id from game_has_labels where game_id = "+String.valueOf(id)+")";
        return getLabelsFromDb(sql);
    }

    public void insertLabel(Label label) {
        String sql = "insert into label (id, name, description, requires, is_authorized, user_id) values (DEFAULT, '"+label.getName()+"', " +
                "'"+label.getDescription()+"', FALSE, "+label.isAuthorized()+"," + label.getUserId()+")";
        try {
            Statement stmt = db.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Label getLabelByName(String labelName) {
        String sql = "select * from label where name = '" + labelName + "'";
        List<Label> labels = getLabelsFromDb(sql);
        if(labels.size() > 0) {
            return labels.get(0);
        } else {
            return null;
        }
    }

    public List<Label> getLabelsByNames(List<String> labelsList) {
        List<Label> result = new ArrayList<Label>();
        Label label;
        for(String labelStr: labelsList){
            label = getLabelByName(labelStr);
            if(label != null){
                result.add(label);
            }
        }
        return result;
    }
}
