package cz.larpovadatabaze.components.page.search;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.HbUtils;
import cz.larpovadatabaze.utils.Strings;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.*;

/**
 * Provides games matching the query.
 * TODO Currently gets all games and does the filtering itself - consider rewriting to some more efficient approach - TODO
 */
public class UserSearchProvider extends SortableDataProvider<CsldUser, String> {

    private static final int DEFAULT_MAX_RESULTS = 50;
    /**
     * List of users - not to be serialized
     */
    private transient List<CsldUser> filteredUsers;

    @SpringBean
    private CsldUserService userService;


    private class UserModel extends LoadableDetachableModel<CsldUser> {

        // Game id. We could also store id as page property.
        private int gameId;

        private UserModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected CsldUser load() {
            CsldUser user = userService.getById(gameId);
            if(HbUtils.isProxy(user)){
            }    user = HbUtils.deproxy(user);

            return user;
        }
    }

    private String query;

    private int maxResults = DEFAULT_MAX_RESULTS;

    private boolean moreAvailable;

    public UserSearchProvider() {
        Injector.get().inject(this);
    }

    public void setQuery(String query) {
        this.query = query;
        this.filteredUsers = null; // Clear games
    }

    public List<CsldUser> getUsersList() {
        if (filteredUsers == null) {
            // Load games
            List<CsldUser> allResults = new ArrayList<>(new LinkedHashSet<>(userService.getAll()));
            filteredUsers = new ArrayList<>();
            Collator collator = Collator.getInstance(new Locale("cs"));
            collator.setStrength(Collator.PRIMARY);

            moreAvailable = false;
            for (CsldUser result : allResults) {
                if (Strings.containsIgnoreCaseAndAccents(result.getAutoCompleteData(), query)) {
                    if (filteredUsers.size() >= maxResults) {
                        // Got enough...
                        moreAvailable = true;
                        break;
                    }
                    filteredUsers.add(result);
                }
            }
        }

        return filteredUsers;
    }

    @Override
    public Iterator<? extends CsldUser> iterator(long first, long count) {
        return getUsersList().subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return getUsersList().size();
    }

    @Override
    public IModel<CsldUser> model(CsldUser object) {
        return new UserModel(object.getId());
    }

    /**
     * @param maxResults Maximum results returned
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * @return Whether there is more results available
     */
    public boolean isMoreAvailable() {
        return moreAvailable;
    }
}
