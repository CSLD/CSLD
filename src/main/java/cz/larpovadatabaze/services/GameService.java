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

    public boolean addGame(Game game);

    public void editGame(Game game);

    List<Game> getRated(long first, long amountPerPage);

    List<Game> getOrderedByName(long first, long amountPerPage);

    List<Game> getRatedAmount(long first, long amountPerPage);

    List<Game> getCommentedAmount(long first, long amountPerPage);

    List<Game> getSimilar(Game game);

    List<Game> gamesOfAuthors(Game game);

    List<Game> getByAutoCompletable(String gameName) throws WrongParameterException;

    Game getBestGame(CsldUser actualAuthor);

    Game getRandomGame();

    List<Game> getLastGames(int amountOfGames);

    int getAmountOfGames();
}
