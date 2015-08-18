package cz.larpovadatabaze.services.builders;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.utils.Pwd;

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

        save(user);
        return user;
    }

    public CsldGroup group(String description, String lang, String name) {
        CsldGroup group = new CsldGroup();
        group.setDescription(description);
        group.setLang(lang);
        group.setName(name);

        save(group);
        return group;
    }

    public Label label(CsldUser owner, String name, String lang, String description, boolean authorized) {
        Label label = new Label();
        label.setAuthorized(authorized);
        label.setDescription(description);
        label.setName(name);
        label.setLang(lang);
        label.setAddedBy(owner);

        save(label);
        return label;
    }

    public Game game(String name, String description, String lang, CsldUser addedBy, List<CsldUser> authors,
                     List<Label> labels, Timestamp added) {
        return game(name, description, lang, addedBy, authors, labels, added, null);
    }

    public Game game(String name, String description, String lang, CsldUser addedBy, List<CsldUser> authors,
                     List<Label> labels, Timestamp added, Image coverImage) {
        Game game = new Game();
        game.setTotalRating(0d);
        game.setName(name);
        game.setDescription(description);
        game.setLang(lang);
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
        comment.setLang("cs");

        save(comment);
        return comment;
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