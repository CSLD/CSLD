package org.pilirion.models.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 12:37
 */
public class Game {
    private int id = -1;
    private String name;
    private String image;
    private int menRoles = -1;
    private int womenRoles = -1;
    private int hours = -1;
    private int days = -1;
    private int year = -1;
    private String description = null;
    private Date premier;
    private int players = -1;
    private int bothRole = -1;
    private int userAdded = -1;
    private double ratingZebricek;

    private List<Label> labels;
    private List<Author> authors;
    private List<Comment> comments;
    private List<Rating> ratings;

    public Game(int id, String name, String image, int menRoles, int womenRoles, int bothRole, int hours, int days, int year,
                String description, Date premier, int players,
                List<Author> authors, List<Comment> comments, List<Rating> ratings, List<Label> labels){
        this.name = name;
        this.authors = authors;
        this.id = id;
        this.year = year;
        this.description = description;
        this.comments = comments;
        this.ratings = ratings;
        this.image = image;
        this.labels = labels;
        this.menRoles = menRoles;
        this.womenRoles = womenRoles;
        this.hours = hours;
        this.days = days;
        this.premier = premier;
        this.players = players;
        this.bothRole = bothRole;
    }

    public void setRatingZebricek(double ratingZebricek){
        this.ratingZebricek = ratingZebricek;
    }

    public double getRatingZebricek(){
        return ratingZebricek;
    }

    public boolean isValid(){
        return getName() != null && getDescription() != null;
    }

    public int getRatingPercents() {
        List<Rating> ratings = getRatings();
        int wholeRating = 0;
        int amountOfRatings = 0;
        for(Rating rating: ratings){
            wholeRating += rating.getRating();
            amountOfRatings++;
        }
        wholeRating *= 10;
        if(amountOfRatings == 0){
            return 0;
        }
        return wholeRating / amountOfRatings;
    }

    public String getRatingColor() {
        String red = "#c1001f";
        String blue = "#0072bc";
        String gray = "#535353";
        if(getRatings().size() < 4 ){
            return "#aaa";
        }

        int rating = getRatingPercents();
        if(rating < 30){
            return gray;
        } else if (rating > 30 && rating < 70) {
            return blue;
        } else {
            return red;
        }
    }

    public String getShortenedDescription() {
        if(description.length() > 100){
            return description.substring(0,100) + " ...";
        }
        return description;
    }

    public String getLabelsText() {
        List<Label> gameLabels = getLabels();
        String labelsText = "";
        for(Label label: gameLabels){
            labelsText += "<span>" + label.getName() + "</span> / ";
        }
        if(!labelsText.equals("")){
            labelsText = labelsText.substring(0, labelsText.length() - 3);
        }
        return labelsText;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getAuthorsText() {
        List<Author> authors = getAuthors();
        String authorsText = "";
        for(Author author: authors){
            authorsText += "<a href='autor.jsp?id="+author.getId()+"'>" + author.getPerson().getName() + "</a>, ";
        }
        if(!authorsText.equals("")){
            authorsText = authorsText.substring(0, authorsText.length() - 2);
        }
        return authorsText;
    }

    public int getHours() {
        return hours;
    }

    public int getDays() {
        return days;
    }

    public String getName() {
        return name;
    }

    public List<Author> getAuthors() {
        if(authors == null){
            authors = new ArrayList<Author>();
        }
        return authors;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getBothRole(){
        return bothRole;
    }

    public String getDescription() {
        return description;
    }

    public List<Comment> getComments() {
        if(comments == null){
            comments = new ArrayList<Comment>();
        }
        return comments;
    }

    public List<Rating> getRatings() {
        if(ratings == null){
            ratings = new ArrayList<Rating>();
        }
        return ratings;
    }

    public int getPlayersAmount() {
        return players;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getImage() {
        if (image == null || image.equals("")) {
            image = "/img/icon/question_icon_game.png";
        }
        return image;
    }

    public List<Label> getLabels() {
        if(labels == null){
            labels = new ArrayList<Label>();
        }
        return labels;
    }

    public int getMenRoles() {
        return menRoles;
    }

    public int getWomenRoles() {
        return womenRoles;
    }

    public String getPremier() {
        if(premier == null){
            return null;
        }
        return premier.toString();  //To change body of created methods use File | Settings | File Templates.
    }

    public int getPlayers() {
        return players;
    }

    public Label getRequiredLabel() {
        for(Label label: getLabels()){
            if(label.isRequires()){
                return label;
            }
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean hasLabels(List<String> usedLabels) {
        boolean thisHas;
        for(String labelStr: usedLabels){
            thisHas = false;
            for(Label label: getLabels()){
                if(label.getName().equals(labelStr)){
                    thisHas = true;
                }
            }
            if(!thisHas){
                return false;
            }
        }
        return true;
    }

    public void setUserWhoAddedGame(int userId) {
        this.userAdded = userId;
        //To change body of created methods use File | Settings | File Templates.
    }

    public void setImage(String path){
        this.image = path;
    }

    public int getUserWhoAddedGame() {
        return userAdded;
    }
}
