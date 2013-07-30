package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GameDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.GameService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 9.4.13
 * Time: 11:19
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
    public List<Game> getRated(){
        return gameDAO.getRated();
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
    public void addGame(Game game) {
        gameDAO.saveOrUpdate(game);
    }

    @Override
    public void editGame(Game game) {
        gameDAO.saveOrUpdate(game);
    }

    @Override
    public void flush() {
        gameDAO.flush();
    }

    @Override
    public List<Game> getGamesOfAuthor(Integer id) {
        Criterion criterion = Restrictions.eq("author", id);
        return gameDAO.findByCriteria(criterion);
    }

    @Override
    public List<Game> getOrderedByName() {
        return gameDAO.getOrderedByName();
    }

    @Override
    public List<Game> getRatedAmount() {
        return gameDAO.getRatedAmount();
    }

    @Override
    public List<Game> getCommentedAmount() {
        return gameDAO.getCommentedAmount();
    }

    @Override
    public List<Game> getSimilar(Game game) {
        Game exampleGame = new Game();
        exampleGame.setLabels(game.getLabels());
        return gameDAO.findByExample(exampleGame, new String[]{});
    }

    @Override
    public List<Game> gamesOfAuthors(Game game) {
        List<Game> games = new ArrayList<Game>();
        for(CsldUser author: game.getAuthors()){
            games.addAll(author.getAuthorOf());
        };
        return games;
    }

    @Override
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        return gameDAO.getByAutoCompletable(gameName);
    }
}
