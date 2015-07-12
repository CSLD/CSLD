package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.panel.news.CreateOrUpdateNewsPanel;
import cz.larpovadatabaze.components.panel.news.NewsListPanel;
import cz.larpovadatabaze.entities.*;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.game.CommentsListPanel;
import cz.larpovadatabaze.components.panel.game.GameListPanel;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.components.panel.user.PersonDetailPanel;
import cz.larpovadatabaze.components.panel.user.RatingsListPanel;
import cz.larpovadatabaze.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.RatingService;
import cz.larpovadatabaze.utils.HbUtils;
import cz.larpovadatabaze.utils.UserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 17:30
 */
public class UserDetailPage extends CsldBasePage {
    public static final String USER_ID_PARAMETER_NAME = "id";

    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    GameService gameService;
    @SpringBean
    RatingService ratingService;

    private ModalWindow createNewsModal;

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
            CsldUser res = csldUserService.getById(userId);
            if (HbUtils.isProxy(res)) {
                res = HbUtils.deproxy(res);
            }
            return res;
        }
    }

    public UserDetailPage(PageParameters params){
        if(params.get(USER_ID_PARAMETER_NAME) == null || params.get(USER_ID_PARAMETER_NAME).isEmpty()){
            throw new RestartResponseException(HomePage.class);
        }
        setDefaultModel(new UserModel(params.get(USER_ID_PARAMETER_NAME).to(Integer.class)));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser user = (CsldUser)getDefaultModelObject();

        add(new PersonDetailPanel("personDetail", (IModel<CsldUser>)getDefaultModel()));

        BookmarkablePageLink updateUserLink = new BookmarkablePageLink<UpdateUserPage>("updateUserLink", UpdateUserPage.class);
        updateUserLink.setVisibilityAllowed(CsldAuthenticatedWebSession.get().getLoggedUser() != null);
        add(updateUserLink);

        add(new NewsListPanel("news", user.getId()));

        add(new CommentsListPanel("comments", new UserCommentsModel(), true));

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(gameService);
        provider.setAuthor(user);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));

        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(HbUtils.isProxy(user)){
            user = HbUtils.deproxy(user);
        }

        // Fill played games from rated games
        List<IGameWithRating> playedGames = new ArrayList<IGameWithRating>();
        playedGames.addAll(ratingService.getRatingsOfUser(logged, user));

        // Build set of rated games IDs
        Set<Integer> ratedGamesIds = new HashSet<Integer>();
        for(IGameWithRating r : playedGames) {
            ratedGamesIds.add(r.getGame().getId());
        }

        // Pass user's games, build list of wanted games, add played games to ratings
        List<Game> wantedGames = new ArrayList<Game>();
        for(UserPlayedGame played : user.getPlayedGames()){
            if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY)){
                // Add to wanted games
                wantedGames.add(played.getPlayedBy());
            } else if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.PLAYED)) {
                if (!ratedGamesIds.contains(played.getGameId())) {
                    // Add to list of played games, without rating
                    playedGames.add(new GameWithoutRating(played.getPlayedBy()));
                }
            }
        }

        createNewsModal = new ModalWindow("createNews");
        createNewsModal.setTitle("Novinky");
        createNewsModal.setCookieName("create-news");
        add(createNewsModal);

        AjaxLink createLink = new AjaxLink("createNewsLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                createNewsModal.setContent(new CreateOrUpdateNewsPanel(createNewsModal.getContentId()){
                    @Override
                    protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                        super.onCsldAction(target, form);
                        createNewsModal.close(target);
                    }
                });
                createNewsModal.show(target);
            }
        };
        add(createLink);
        if(logged == null || !user.getId().equals(logged.getId())) {
            createLink.setVisibilityAllowed(false);
        }

        // Add player games
        add(new RatingsListPanel("ratedGames", Model.ofList(playedGames)));

        // Add wanted games
        add(new GameListPanel("wantedGamesPanel",Model.ofList(wantedGames)));
    }
}
