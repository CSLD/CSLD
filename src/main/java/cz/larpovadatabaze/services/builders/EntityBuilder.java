package cz.larpovadatabaze.services.builders;

import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.utils.Pwd;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Supporting class, which allows user to create and store entities.
 */
@Service
public class EntityBuilder {
    @Autowired
    private SessionFactory persistenceStore;

    public CsldUser user(String email, String name, String nickName, String city, String description, Short role,
                         String password) {
        CsldUser user = new CsldUser();
        Person person = new Person();
        person.setEmail(email);
        person.setName(name);
        person.setNickname(nickName);
        person.setBirthDate(new Date(Calendar.getInstance().getTime().getTime()));
        person.setCity(city);
        person.setDescription(description);

        user.setPerson(person);
        user.setPassword(Pwd.generateStrongPasswordHash(password, person.getEmail()));
        user.setAmountOfComments(1);
        user.setAmountOfCreated(1);
        user.setAmountOfPlayed(1);
        user.setDefaultLang("cs");
        user.setIsAuthor(false);
        user.setRole(role);
        user.setDefaultLang("cs");

        save(user);
        return user;
    }

    public CsldGroup group(String name) {
        CsldGroup group = new CsldGroup();
        group.setName(name);

        save(group);
        return group;
    }

    public Label label(CsldUser owner, String name, String description, boolean authorized, boolean required) {
        Label label = new Label();
        label.setAuthorized(authorized);
        label.setDescription(description);
        label.setName(name);
        label.setAddedBy(owner);
        label.setRequired(required);

        save(label);
        return label;
    }

    public Game game(String name, String description, CsldUser addedBy, List<CsldUser> authors,
                     List<Label> labels, Timestamp added) {
        return game(name, description, addedBy, authors, labels, added, null);
    }

    public Game game(String name, String description, CsldUser addedBy, List<CsldUser> authors,
                     List<Label> labels, Timestamp added, Image coverImage) {
        return game(name, description, addedBy, authors, labels, added, coverImage, 0);
    }

    public Game game(String name, String description, CsldUser addedBy, List<CsldUser> authors,
                     List<Label> labels, Timestamp added, Image coverImage, Integer year) {
        Game game = new Game();
        game.setTotalRating(0d);
        game.setName(name);
        game.setDescription(description);
        game.setAddedBy(addedBy);
        game.setAdded(added);
        game.setAmountOfComments(0);
        game.setAmountOfRatings(0);
        game.setAmountOfPlayed(0);
        game.setAuthors(authors);
        game.setAverageRating(0.0);
        game.setMenRole(4);
        game.setWomenRole(5);
        game.setLabels(labels);
        game.setCoverImage(coverImage);
        game.setYear(year);

        for(CsldUser author: authors) {
            if(author.getAuthorOf() == null) {
                author.setAuthorOf(new ArrayList<>());
            }
            author.getAuthorOf().add(game);
        }

        save(game);
        return game;
    }

    public Rating rating(CsldUser user, Game game, int ratingValue) {
        Rating rating = new Rating();
        rating.setAdded(Timestamp.from(Instant.now()));
        rating.setGame(game);
        rating.setUser(user);
        rating.setRating(ratingValue);

        save(rating);
        return rating;
    }

    public Comment comment(CsldUser user, Game game, String commentText) {
        Comment comment = new Comment();
        comment.setGame(game);
        comment.setUser(user);
        comment.setComment(commentText);
        comment.setAdded(Timestamp.from(Instant.now()));

        save(comment);
        return comment;
    }

    public Upvote plusOne(CsldUser user, Comment comment) {
        Upvote upvote = new Upvote();
        upvote.setComment(comment);
        upvote.setUser(user);
        upvote.setAdded(new Timestamp(new java.util.Date().getTime()));

        save(upvote);
        return upvote;
    }

    public UserPlayedGame playerOfGame(CsldUser user, Game game) {
        UserPlayedGame playerOfGame = new UserPlayedGame();
        playerOfGame.setGame(game);
        playerOfGame.setPlayerOfGame(user);
        playerOfGame.setStateEnum(UserPlayedGame.UserPlayedGameState.PLAYED);

        return playerOfGame;
    }

    public News news(CsldUser editor, String text) {
        News news = new News();
        news.setAuthor(editor);
        news.setText(text);
        news.setLang("cs");
        news.setAdded(Timestamp.from(Instant.now()));

        return null;
    }

    public void flush() {
        persistenceStore.getCurrentSession().flush();
    }

    /**
     * To be changed in order to use JPA EntityManager.
     * @param o Object to save in persistent store. Basically it is either JPA entity or hibernate entity.
     */
    private void save(Object o) {
        persistenceStore.getCurrentSession().save(o);
    }
}