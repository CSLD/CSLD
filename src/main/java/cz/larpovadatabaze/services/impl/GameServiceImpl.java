package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.dao.GameDAO;
import cz.larpovadatabaze.dao.GameHasLanguageDao;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.Strings;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
    private static final int GAME_ICON_SIZE=120;

    @Autowired private GameDAO gameDAO;
    @Autowired private SessionFactory sessionFactory;
    @Autowired private FileService fileService;
    @Autowired private ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;
    @Autowired private ImageService imageService;
    @Autowired private LanguageSolver languageSolver;
    @Autowired private GameHasLanguageDao gameHasLanguageDao;

    private ResourceReference iconResourceReference;

    @Override
    public Game getById(Integer id) {
        return gameDAO.findById(id);
    }

    @Override
    public void evictGame(Integer id) {
        Game g1 = (Game)sessionFactory.getCurrentSession().get(Game.class, id);
        sessionFactory.getCurrentSession().evict(g1);
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
        if (game.getImage() != null) {
            if (game.getImage().getPath() == null) game.setImage(null);
        }
        if(game.getWeb() != null && !game.getWeb().isEmpty() && (!game.getWeb().startsWith("http://") &&
                !game.getWeb().startsWith("https://"))) {
            game.setWeb("http://" + game.getWeb());
        }

        return gameDAO.saveOrUpdate(game);
    }

    @Override
    public List<Game> getSimilar(Game game) {
        return gameDAO.getSimilar(game);
    }

    @Override
    public List<Game> gamesOfAuthors(Game game) {
        // TODO sort games by rating.
        Set<Game> games = new LinkedHashSet<Game>();
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
    public Game getRandomGame() {
        return gameDAO.getRandomGame();
    }

    @Override
    public Collection<Game> getLastGames(int amountOfGames) {
        return new LinkedHashSet<Game>(gameDAO.getLastGames(amountOfGames, languageSolver.getLanguagesForUser()));
    }

    @Override
    public int getAmountOfGames() {
        return gameDAO.getAmountOfGames();
    }

    @Override
    public List<Game> getFilteredGames(FilterGame filterGame, List<Label> labels, int offset, int limit, Order orderBy) {
        return gameDAO.getFilteredGames(filterGame, labels, offset, limit, orderBy);
    }

    @Override
    public long getAmountOfFilteredGames(FilterGame filterGame, List<Label> labels) {
        return gameDAO.getAmountOfFilteredGames(filterGame, labels);
    }

    @Override
    public Collection<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        return new LinkedHashSet<Game>(gameDAO.getGamesOfAuthor(author, first, count));
    }

    @Override
    public Collection<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count) {
        return new LinkedHashSet<Game>(gameDAO.getGamesOfGroup(csldGroup, first, count));
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

        CsldUser logged = (CsldAuthenticatedWebSession.get()).getLoggedUser();
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
        if(game.getAverageRating() == null){
            game.setAverageRating(0d);
        }
        if(game.getDescription() != null){
            game.setDescription(Jsoup.clean(game.getDescription(), Whitelist.basic()));
        }

        final List<FileUpload> uploads = (game.getImage() != null)?game.getImage().getFileUpload():null;
        if (uploads != null && uploads.size() > 0) {
            FileUpload upload = uploads.get(0);
            String filePath = fileService.saveImageFileAndReturnPath(upload, imageResizingStrategyFactoryService.getCuttingSquareStrategy(GAME_ICON_SIZE, 50)).path;
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

                return addGame(game);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to write file", e);
            }
        } else {
            if(game.getVideo() == null ||
                    game.getVideo().getPath() == null ||
                    game.getVideo().getPath().equals("") ||
                    game.getVideo().getPath().equals("Video")){
                game.setVideo(null);
            }

            return addGame(game);
        }
    }

    @Override
    public boolean canEditGame(Game game) {
        CsldUser loggedUser = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(loggedUser != null){
            for(CsldUser author : game.getAuthors()) {
                if (author.getId().equals(loggedUser.getId())) {
                    return true;
                }
            }
            if(loggedUser.getRole() >= CsldRoles.ADMIN.getRole()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Game> getGamesCommentedByUser(int userId) {
        return new LinkedHashSet<Game>(gameDAO.getGamesCommentedByUser(userId));
    }

    @Override
    public boolean isHidden(int gameId) {
        Game game = getById(gameId);
        return game != null && game.isDeleted();
    }

    @Override
    public String getTextStateOfGame(int gameId) {
        if(isHidden(gameId)) {
            return Strings.getResourceString(CsldBasePage.class, "game.show");
        } else {
            return Strings.getResourceString(CsldBasePage.class, "game.delete");
        }
    }

    @Override
    public void toggleGameState(int gameId) {
        Game game = gameDAO.findById(gameId);
        if(game == null) {
            return;
        }
        game.setDeleted(!game.isDeleted());
        gameDAO.saveOrUpdate(game);
    }

    @Override
    public Collection<Game> getGamesRatedByUser(int userId) {
        return new LinkedHashSet<Game>(gameDAO.getGamesRatedByUser(userId));
    }

    @Override
    public void deleteTranslation(GameHasLanguages toRemove) {
        gameHasLanguageDao.makeTransient(toRemove);
    }

    @Override
    public ResourceReference getIconReference() {
        synchronized (this) {
            if (iconResourceReference == null) {
                iconResourceReference = imageService.createImageTypeResourceReference(gameDAO);
            }
        }

        return iconResourceReference;
    }
}
