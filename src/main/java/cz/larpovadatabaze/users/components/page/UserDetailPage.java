package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.games.components.panel.CommentsListPanel;
import cz.larpovadatabaze.games.components.panel.GameListPanel;
import cz.larpovadatabaze.games.components.panel.ListGamesWithAnnotations;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.games.services.GamesWithState;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.panel.PersonDetailPanel;
import cz.larpovadatabaze.users.components.panel.RatingsListPanel;
import cz.larpovadatabaze.users.providers.SortableAnnotatedProvider;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 17:30
 */
public class UserDetailPage extends CsldBasePage {
    public static final String USER_ID_PARAMETER_NAME = "id";

    @SpringBean
    CsldUsers csldUsers;
    @SpringBean
    AuthoredGames authoredGames;
    @SpringBean
    GamesWithState games;
    @SpringBean
    AppUsers appUsers;

    private class UserCommentsModel extends LoadableDetachableModel<List<Comment>> {

        @Override
        protected List<Comment> load() {
            List<Comment> userComments = new ArrayList<>();

            CsldUser thisUser = (CsldUser) getDefaultModelObject();
            List<Comment> allUserComments = thisUser.getCommented();

            // Add comments
            if (appUsers.isEditor()) {
                // Editors see all, users also see all their comments
                userComments.addAll(allUserComments);
            } else {
                // Filter not-hidden comments
                for (Comment c : allUserComments) {
                    if (!Boolean.TRUE.equals(c.getHidden())) {
                        userComments.add(c);
                    }
                }
            }

            // Sort
            Collections.sort(userComments, (o1, o2) -> -1 * o1.getAdded().compareTo(o2.getAdded()));

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
            CsldUser res = csldUsers.getById(userId);
            if (HbUtils.isProxy(res)) {
                res = HbUtils.deproxy(res);
            }
            return res;
        }
    }

    public UserDetailPage(PageParameters params){
        Integer userId;

        if(params.get(USER_ID_PARAMETER_NAME) == null || params.get(USER_ID_PARAMETER_NAME).isEmpty()) {
            if (CsldAuthenticatedWebSession.get().isSignedIn()) {
                // Show current user
                userId = CsldAuthenticatedWebSession.get().getLoggedUser().getId();
            }
            else {
                // Go to HP
                throw new RestartResponseException(HomePage.class);
            }
        }
        else {
            userId = params.get(USER_ID_PARAMETER_NAME).to(Integer.class);
        }
        setDefaultModel(new UserModel(userId));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CsldUser user = (CsldUser)getDefaultModelObject();

        add(new PersonDetailPanel("personDetail", (IModel<CsldUser>)getDefaultModel()));

        // TODO: Refactor move visibility into the class.
        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
        BookmarkablePageLink updateUserLink = new BookmarkablePageLink<UpdateUserPage>("updateUserLink", UpdateUserPage.class);
        updateUserLink.setVisibilityAllowed(logged != null && logged.getId().equals(user.getId()));
        add(updateUserLink);

        add(new CommentsListPanel("comments", new UserCommentsModel(), true));

        SortableAnnotatedProvider provider = new SortableAnnotatedProvider(authoredGames);
        provider.setAuthor(user);
        add(new ListGamesWithAnnotations("annotatedGamesOfAuthor", provider));

        if (HbUtils.isProxy(user)) {
            user = HbUtils.deproxy(user);
        }

        // Add player games
        add(new RatingsListPanel("ratedGames", Model.ofList(
                games.getPlayedByUser(user))));

        // Add wanted games
        add(new GameListPanel("wantedGamesPanel", Model.ofList(
                games.getWantedByUser(user))));

        add(new org.apache.wicket.markup.html.basic.Label("ical", Model.of("http://larpovadatabaze.cz/ical?id=" + user.getId())));
    }

    public static PageParameters paramsForUser(CsldUser user) {
        PageParameters pp = new PageParameters();

        if (user != null) {
            pp.add(USER_ID_PARAMETER_NAME, user.getId());
        }

        return pp;
    }
}
