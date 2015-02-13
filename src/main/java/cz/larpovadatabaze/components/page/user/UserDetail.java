package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.CommentsListPanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.user.PersonDetailPanel;
import cz.larpovadatabaze.components.panel.user.RatingsListPanel;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.utils.HbUtils;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 17:30
 */
public class UserDetail extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    GameService gameService;
    @SpringBean
    RatingService ratingService;

    private class UserCommentsModel extends LoadableDetachableModel<List<Comment>> {

        @Override
        protected List<Comment> load() {
            List<Comment> userComments = new ArrayList<Comment>();

            CsldUser thisUser = (CsldUser)getDefaultModelObject();
            List<Comment> allUserComments = thisUser.getCommented();

            // Add comments
            if (UserUtils.isEditor() || (thisUser.equals(UserUtils.getLoggedUser()))) {
                // Editors see all, users also see all their comments
                userComments.addAll(allUserComments);
            }
            else {
                // Filter not-hidden comments
                for(Comment c : allUserComments) {
                    if (!Boolean.TRUE.equals(c.getHidden())) userComments.add(c);
                }
            }

            // Sort
            Collections.sort(userComments, new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    int compared = o1.getAdded().compareTo(o2.getAdded());
                    if(compared == 0) {
                        return 0;
                    }
                    else if(compared == -1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            return userComments;
        }
    }

    private class UserModel extends LoadableDetachableModel<CsldUser> {

        final int userId;

        private UserModel(int userId) {
            this.userId = userId;
        }

        @Override
        protected CsldUser load() {
            return csldUserService.getById(userId);
        }
    }

    public UserDetail(PageParameters params){
        if(params.get("id") == null || params.get("id").isEmpty()){
            throw new RestartResponseException(ListUser.class);
        }
        setDefaultModel(new UserModel(params.get("id").to(Integer.class)));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser user = (CsldUser)getDefaultModelObject();

        add(new PersonDetailPanel("personDetail", (IModel<CsldUser>)getDefaultModel()));
        add(new CommentsListPanel("comments", new UserCommentsModel(), true));

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(gameService);
        provider.setAuthor(user);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));

        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(HbUtils.isProxy(user)){
            user = HbUtils.deproxy(user);
        }

        WebMarkupContainer ratingLabel = new WebMarkupContainer("ratingLabel");
        add(ratingLabel);

        List<Game> playedGames = new ArrayList<Game>();
        List<Game> wantedGames = new ArrayList<Game>();
        for(UserPlayedGame played: user.getPlayedGames()){
            if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY)){
                wantedGames.add(played.getPlayedBy());
            } else if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.PLAYED)) {
                playedGames.add(played.getPlayedBy());
            } else {

            }
        }

        if(logged == null || (!logged.getId().equals(user.getId()) && !csldUserService.isLoggedAtLeastEditor())) {
            // Do not show rated games
            add(new WebMarkupContainer("ratedGames").setVisible(false));
            ratingLabel.setVisible(false);
        }
        else {
            // Load ratings
            List<Rating> myRatings = ratingService.getRatingsOfUser(logged, user);
            Collections.sort(myRatings, new Comparator<Rating>() {
                @Override
                public int compare(Rating o1, Rating o2) {
                    return o2.getRating() - o1.getRating();
                }
            });
            add(new RatingsListPanel("ratedGames", Model.ofList(myRatings)));

            // From played games, remove those that are rated, so they do not show twice
            Set<Game> playedGamesSet = new HashSet<Game>(playedGames);
            for(Rating r : myRatings) {
                playedGamesSet.remove(r.getGame());
            }
            playedGames.clear();
            playedGames.addAll(playedGamesSet);
        }
        add(new GameListPanel("playedGames",Model.ofList(playedGames)));

        add(new GameListPanel("wantedGamesPanel",Model.ofList(wantedGames)));
    }
}
