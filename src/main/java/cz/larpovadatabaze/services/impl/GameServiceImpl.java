package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GameDAO;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.FileUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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
    public List<Game> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
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

    @Override
    public List<Game> getFilteredGames(FilterGame filterGame, List<Label> labels, int offset, int limit, String orderBy) {
        return gameDAO.getFilteredGames(filterGame, labels, offset, limit, orderBy);
    }

    @Override
    public long getAmountOfFilteredGames(FilterGame filterGame, List<Label> labels) {
        return gameDAO.getAmountOfFilteredGames(filterGame, labels);
    }

    @Override
    public List<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        return gameDAO.getGamesOfAuthor(author, first, count);
    }

    @Override
    public List<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count) {
        return gameDAO.getGamesOfGroup(csldGroup, first, count);
    }

    @Override
    public long getAmountOfGamesOfAuthor(CsldUser author) {
        return gameDAO.getAmountOfGamesOfAuthor(author);
    }

    @Override
    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        return gameDAO.getAmountOfGamesOfGroup(csldGroup);
    }



    public boolean saveOrUpdate(Game game) {
        game.setAdded(new Timestamp(new Date().getTime()));

        CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
        game.setAddedBy(logged);

        if(game.getAmountOfComments() == null){
            game.setAmountOfComments(0);
        }
        if(game.getAmountOfRatings() == null){
            game.setAmountOfRatings(0);
        }
        if(game.getAmountOfPlayed() == null){
            game.setAmountOfPlayed(0);
        }
        if(game.getTotalRating() == null){
            game.setTotalRating(0d);
        }
        if(game.getImage() == null || (game.getImage().getFileUpload() == null && game.getImage().getPath() == null)) {
            game.setImage(Image.getDefaultGame());
        }
        if(game.getDescription() != null){
            game.setDescription(Jsoup.clean(game.getDescription(), Whitelist.basic()));
        }

        final List<FileUpload> uploads = game.getImage().getFileUpload();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                String filePath = FileUtils.saveImageFileAndReturnPath(upload, game.getName(), 120, 120);
                try {
                    Image image = new Image();
                    image.setPath(filePath);
                    game.setImage(image);

                    if(game.getVideo() == null ||
                            game.getVideo().getPath() == null ||
                            game.getVideo().getPath().equals("") ||
                            game.getVideo().getPath().equals("Video")){
                        //TODO problem when internationalizating.
                        game.setVideo(null);
                    }

                    if(addGame(game)) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            if(game.getVideo() == null ||
                    game.getVideo().getPath() == null ||
                    game.getVideo().getPath().equals("") ||
                    game.getVideo().getPath().equals("Video")){
                game.setVideo(null);
            }

            if(addGame(game)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
