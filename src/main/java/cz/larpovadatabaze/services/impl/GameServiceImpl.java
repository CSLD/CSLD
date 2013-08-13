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

import java.util.*;

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
        // TODO decide what actually does similarity means. I propose that larp is more similar if it had similar players
        // and if it has same labels. The more labels are same the better. Similarity 1 means labels and players are the same.
        // Similarity 0 means no player and no label were the same.
        return gameDAO.findByExample(exampleGame, new String[]{});
    }

    @Override
    public List<Game> gamesOfAuthors(Game game) {
        // TODO sort games by rating.
        Set<Game> games = new HashSet<Game>();
        for(CsldUser author: game.getAuthors()){
            games.addAll(author.getAuthorOf());
        };
        List<Game> gamesOfAuthors = new ArrayList<Game>();
        gamesOfAuthors.addAll(games);
        return gamesOfAuthors;
    }

    @Override
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        return gameDAO.getByAutoCompletable(gameName);
    }

    @Override
    public double getRatingOfGame(Game game) {
        return gameDAO.getRatingOfGame(game);
    }

    @Override
    public Game getBestGame(CsldUser actualAuthor) {
        return gameDAO.getBestGame(actualAuthor);
    }

    @Override
    public Game getRandomGame() {
        List<Game> all = getAll();
        if(all.size() < 1){
            return null;
        }
        int randomGame = new Random().nextInt(all.size());
        return all.get(randomGame);
    }

    @Override
    public List<Game> getLastGames() {
        return gameDAO.getLastGames();
    }
}
