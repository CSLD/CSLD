package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Game;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 11:19
 */
public interface GameService extends GenericService<Game> {
    public Game getById(Integer id);

    public List<Game> getRated();

    public void addGame(Game game);

    public void editGame(Game game);

    void flush();

    List<Game> getGamesOfAuthor(Integer id);

    List<Game> getOrderedByName();

    List<Game> getRatedAmount();

    List<Game> getCommentedAmount();

    List<Game> getSimilar(Game game);

    List<Game> gamesOfAuthors(Game game);
}
