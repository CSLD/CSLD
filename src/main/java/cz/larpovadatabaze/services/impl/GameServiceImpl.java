package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GameDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Repository
public class GameServiceImpl implements GameService {
    @Autowired
    private GameDAO gameDAO;

    @Override
    public Game getById(Integer id) {
        return gameDAO.findById(id, false);
    }

    @Override
    public void remove(Game toRemove) {
        gameDAO.makeTransient(toRemove);
    }

    @Override
    public List<Game> getAll() {
        return gameDAO.findAll();
    }

    @Override
    public List<Game> getUnique(Game example) {
        return gameDAO.findByExample(example, new String[]{});
    }


    @Override
    public boolean addGame(Game game) {
        return gameDAO.saveOrUpdate(game);
    }

    @Override
    public void editGame(Game game) {
        gameDAO.saveOrUpdate(game);
    }

    @Override
    public List<Game> getRated(long first, long amountPerPage){
        return gameDAO.getRated(first, amountPerPage);
    }

    @Override
    public List<Game> getOrderedByName(long first, long amountPerPage) {
        return gameDAO.getOrderedByName(first, amountPerPage);
    }

    @Override
    public List<Game> getRatedAmount(long first, long amountPerPage) {
        return gameDAO.getRatedAmount(first, amountPerPage);
    }

    @Override
    public List<Game> getCommentedAmount(long first, long amountPerPage) {
        return gameDAO.getCommentedAmount(first, amountPerPage);
    }

    @Override
    public List<Game> getSimilar(Game game) {
        return gameDAO.getSimilar(game);
    }

    @Override
    public List<Game> gamesOfAuthors(Game game) {
        // TODO sort games by rating.
        Set<Game> games = new HashSet<Game>();
        for(CsldUser author: game.getAuthors()){
            games.addAll(author.getAuthorOf());
        }
        List<Game> gamesOfAuthors = new ArrayList<Game>();
        gamesOfAuthors.addAll(games);
        return gamesOfAuthors;
    }

    @Override
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        return gameDAO.getByAutoCompletable(gameName);
    }

    @Override
    public Game getBestGame(CsldUser actualAuthor) {
        return gameDAO.getBestGame(actualAuthor);
    }

    @Override
    public Game getRandomGame() {
        return gameDAO.getRandomGame();
    }

    @Override
    public List<Game> getLastGames(int amountOfGames) {
        return gameDAO.getLastGames(amountOfGames);
    }

    @Override
    public int getAmountOfGames() {
        return gameDAO.getAmountOfGames();
    }
}
