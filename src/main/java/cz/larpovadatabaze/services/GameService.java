package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface GameService extends GenericService<Game> {
    public Game getById(Integer id);

    public List<Game> getRated();

    public boolean addGame(Game game);

    public void editGame(Game game);

    void flush();

    List<Game> getGamesOfAuthor(Integer id);

    List<Game> getOrderedByName();

    List<Game> getRatedAmount();

    List<Game> getCommentedAmount();

    List<Game> getSimilar(Game game);

    List<Game> gamesOfAuthors(Game game);

    List<Game> getByAutoCompletable(String gameName) throws WrongParameterException;

    public double getRatingOfGame(Game game);

    Game getBestGame(CsldUser actualAuthor);

    Game getRandomGame();

    List<Game> getLastGames();
}
