package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.IGameWithRating;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.games.models.GameWithoutRating;
import cz.larpovadatabaze.games.services.GamesWithState;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cz.larpovadatabaze.common.entities.Rating.GameState.PLAYED;
import static cz.larpovadatabaze.common.entities.Rating.GameState.WANT_TO_PLAY;

@Repository
@Transactional
public class SqlGamesWithState implements GamesWithState {
    private GenericHibernateDAO<Game, Integer> games;
    private GenericHibernateDAO<Rating, Integer> ratings;
    private MailService mails;
    private AppUsers appUsers;

    @Autowired
    public SqlGamesWithState(SessionFactory sessionFactory, AppUsers appUsers, MailService mails) {
        games = new GenericHibernateDAO<>(sessionFactory, new GameBuilder(appUsers));
        ratings = new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Rating.class));
        this.mails = mails;
        this.appUsers = appUsers;
    }

    @Override
    public List<IGameWithRating> getPlayedByUser(CsldUser user) {
        List<Rating> playedByUser = ratings.findByCriteria(Restrictions.and(
                Restrictions.eq("user.id", user.getId()),
                Restrictions.eq("state", PLAYED.ordinal())
        ));

        // If the user doesn't have rights. Nullify the ratings.
        if (appUsers.isAtLeastEditor() ||
                (appUsers.isSignedIn() && appUsers.getLoggedUserId().equals(user.getId()))) {
            return new ArrayList<>(playedByUser);
        } else {
            return playedByUser.stream()
                    .map(rating -> new GameWithoutRating(rating.getGame()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<Game> getWantedByUser(CsldUser user) {
        Criteria criteria = stateByUser(user, WANT_TO_PLAY);

        return games.findByExecutableCriteria(criteria);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public long getAmountOfGamesPlayedBy(CsldUser user) {
        Criteria criteria = stateByUser(user, PLAYED)
                .setProjection(Projections.rowCount());

        List result = criteria.list();
        if (!result.isEmpty()) {
            return (Long) result.get(0);
        }
        return 0L;
    }

    @Override
    public void sendEmailToInterested(Game game, String url) {
        String subject = "[LarpDB] Nová událost, u hry: " + game.getName();
        String content = "" +
                "Byla přidána událost, která se váže ke hře, kterou máte nastavenou jako chci hrát. " +
                "Odkaz: http://larpovadatabaze.cz/" + url;

        List<CsldUser> interested = game.getRatings()
                .stream()
                .filter(rating -> rating.getStateEnum() == WANT_TO_PLAY)
                .map(Rating::getUser)
                .collect(Collectors.toList());
        mails.sendInfoAboutNewEventToAllInterested(interested, subject, content);
    }

    private Criteria stateByUser(CsldUser user, Rating.GameState state) {
        return games.getExecutableCriteria()
                .createAlias("game.ratings", "ratings")
                .add(Restrictions.and(
                        Restrictions.eq("ratings.user.id", user.getId()),
                        Restrictions.eq("ratings.state", state.ordinal())
                ));
    }
}
