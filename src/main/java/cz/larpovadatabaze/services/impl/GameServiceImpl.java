package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GameDAO;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.ResourceReference;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 *
 */
@Repository
@Transactional
public class GameServiceImpl implements GameService {
    @Autowired private GameDAO gameDAO;
    @Autowired private SessionFactory sessionFactory;
    @Autowired private FileService fileService;
    @Autowired private ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;
    @Autowired private ImageService imageService;

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
        return gameDAO.getFirstChoices(startsWith, maxChoices);
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
        if (game.getCoverImage() != null) {
            if (game.getCoverImage().getPath() == null) game.setCoverImage(null);
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
        Set<Game> games = new LinkedHashSet<>();
        for(CsldUser author: game.getAuthors()){
            games.addAll(author.getAuthorOf());
        }
        List<Game> gamesOfAuthors = new ArrayList<>();
        gamesOfAuthors.addAll(games);
        Collections.sort(gamesOfAuthors, (o1, o2) -> {
            if(o1.getTotalRating().equals(o2.getTotalRating())) {
                return 0;
            }
            return o1.getTotalRating() < o2.getTotalRating()? -1: 1;
        });
        return gamesOfAuthors;
    }

    @Override
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        return gameDAO.getByAutoCompletable(gameName);
    }

    @Override
    public List<Game> getLastGames(int amountOfGames) {
        return gameDAO.getLastGames(amountOfGames);
    }

    @Override
    public List<Game> getMostPopularGames(int amountOfGames) {
        return gameDAO.getMostPopularGames(amountOfGames);
    }

    @Override
    public List<Game> getFilteredGames(FilterGame filterGame, int offset, int limit) {
        return gameDAO.getFilteredGames(filterGame, offset, limit);
    }

    @Override
    public long getAmountOfFilteredGames(FilterGame filterGame) {
        return gameDAO.getAmountOfFilteredGames(filterGame);
    }

    @Override
    public Collection<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        return new LinkedHashSet<>(gameDAO.getGamesOfAuthor(author, first, count));
    }

    @Override
    public Collection<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count) {
        return new LinkedHashSet<>(gameDAO.getGamesOfGroup(csldGroup, first, count));
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

        final List<FileUpload> uploads = (game.getCoverImage() != null)?game.getCoverImage().getFileUpload():null;
        if (uploads != null && uploads.size() > 0) {
            FileUpload upload = uploads.get(0);
            String filePath = fileService.saveImageFileAndReturnPath(upload, imageResizingStrategyFactoryService.getCoverImageStrategy()).path;
            try {
                Image image = new Image();
                image.setPath(filePath);
                game.setCoverImage(image);

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
        return new LinkedHashSet<>(gameDAO.getGamesCommentedByUser(userId));
    }

    @Override
    public boolean isHidden(int gameId) {
        Game game = getById(gameId);
        return game != null && game.isDeleted();
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
        return new LinkedHashSet<>(gameDAO.getGamesRatedByUser(userId));
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
