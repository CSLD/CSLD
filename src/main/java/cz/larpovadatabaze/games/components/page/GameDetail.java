package cz.larpovadatabaze.games.components.page;

import cz.larpovadatabaze.administration.components.panel.AdminAllRatingsPanel;
import cz.larpovadatabaze.calendar.component.panel.EventsListLinksOnly;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.common.components.JSPingBehavior;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.tabs.TabsComponentPanel;
import cz.larpovadatabaze.common.entities.Comment;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Video;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.common.utils.Strings;
import cz.larpovadatabaze.games.components.panel.*;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.games.services.SimilarGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.EnclosureContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.*;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.*;

/**
 *
 */
public class GameDetail extends CsldBasePage {
    private static final String ID_PARAM = "id";

    private enum TabContentType {COMMENTS, PHOTOS, VIDEO}

    ;

    @SpringBean
    Games games;
    @SpringBean
    AuthoredGames authoredGames;
    @SpringBean
    SimilarGames similarGames;
    @SpringBean
    Images images;
    @SpringBean
    private AppUsers appUsers;

    private RatingsResultPanel ratingsResult;
    private RatingsPanel ratingsPanel;

    private TabNumberModel tabNumberModel;

    private WebMarkupContainer tabContent;

    private final static Logger logger = Logger.getLogger(GameDetail.class);

    private Vector<TabContentType> tabContentType;

    private IModel<String> previewImageUrlModel;

    /**
     * Model for selected tab number
     */
    private class TabNumberModel implements IModel<Integer> {
        private Integer value;

        private TabNumberModel(int initialValue) {
            this.value = initialValue;
        }

        @Override
        public Integer getObject() {
                  return value;
        }

        @Override
        public void setObject(Integer newValue) {
            if (!value.equals(newValue)) {
                // Value changed
                this.value = newValue;

                // Replace tab panel
                addOrReplaceTabContentPanel();

                // Redraw
                Optional<AjaxRequestTarget> optionalArt = RequestCycle.get().find(AjaxRequestTarget.class);
                optionalArt.ifPresent(ajaxRequestTarget -> ajaxRequestTarget.add(tabContent));
            }
        }

        @Override
        public void detach() {
            // Nothing to do
        }
    }

    /**
     * Model for game specified by game id
     */
    private class GameModel extends LoadableDetachableModel<Game> {

        // Game id. We could also store id as page property.
        private int gameId;

        private GameModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected Game load() {
            logger.debug("Loading game for id "+gameId);

            Game game = games.getById(gameId);
            if(HbUtils.isProxy(game)){
                game = HbUtils.deproxy(game);
            }

            return game;
        }
    }

    /**
     * Model for all events associated with given game.
     */
    private class EventsModel extends LoadableDetachableModel<List<Event>> {
        private GameModel model;

        private EventsModel(int gameId) {
            model = new GameModel(gameId);
        }

        @Override
        protected List<Event> load() {
            return model.load().getEvents();
        }
    }

    /**
     * Model for comments of actual game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     * The results are ordered by the Amount of upvotes. In case of draw it is ordered by the most recent ones.
     */
    private class CommentsModel extends LoadableDetachableModel<List<Comment>> {
        @Override
        public List<Comment> load() {
            List<Comment> res = new ArrayList<Comment>();

            // Fill in array
            if (appUsers.isEditor()) {
                // Editors see everything
                List<Comment> comments = getModel().getObject().getComments() != null ? getModel().getObject().getComments() :
                        new ArrayList<>();
                res.addAll(comments);
            } else {
                // Filter
                Integer thisUserId = null;
                CsldUser user = appUsers.getLoggedUser();
                if (user != null) {
                    thisUserId = user.getId();
                }
                List<Comment> comments = getModel().getObject().getComments() != null ? getModel().getObject().getComments():
                        new ArrayList<>();
                for(Comment c : comments) {
                    if (c.getHidden()) {
                        if (!c.getUser().getId().equals(thisUserId)) continue; // Hidden comment and user is not creator - hide
                    }
                    res.add(c);
                }
            }

            Set<Comment> unique = new HashSet<>(res);
            res = new ArrayList<>(unique);

            // Sort primarily by the amount of upvotes. Secondarily by the most recent.
            res.sort((o1, o2) -> {
                if (o1.getPluses().size() != o2.getPluses().size()) {
                    return o2.getPluses().size() - o1.getPluses().size();
                } else {
                    return -(o1.getAdded().compareTo(o2.getAdded()));
                }
            });

            return res;
        }
    }

    /**
     * Constructor - initialize just model
     */
    public GameDetail(PageParameters params){
        setVersioned(false);
        try {
            if(params.isEmpty() || StringUtils.isEmpty(params.get(ID_PARAM).toString())) {
                throw new RestartResponseException(ListGamePage.class);
            }
            int gameId = params.get(ID_PARAM).to(Integer.class);
            // If the game is deleted and I don't have sufficient rights redirect me to game deleted page.
            if (games.getById(gameId) == null) {
                throw new RestartResponseException(GameWasDeleted.class);
            }

            setDefaultModel(new GameModel(gameId));
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(ListGamePage.class);
        }
    }

    @Override
    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("larpDatabaseTitleForGame", getDefaultModel());
    }

    @Override
    protected IModel<String> getPreviewImageUrlModel() {
        return previewImageUrlModel;
    }

    @Override
    protected Component provideAdvertisementsPanel(String id) {
        Game game = getModel().getObject();
        if (game.getCoverImage() == null || game.getCoverImage().getPath() == null) {
            // Nothing visible
            return new WebMarkupContainer(id).setVisible(false);
        }
        else {
            // Send main picture
            Fragment f = new Fragment(id, "mainPicture", this);

            f.add(new NonCachingImage("mainPicture", images.getImageResource(game.getCoverImage(), null)));

            return f;
        }
    }

    private void addOrReplaceTabContentPanel() {
        Fragment fragment;

        switch(tabContentType.get(tabNumberModel.getObject())) {
            case COMMENTS:
                // Create comments
                fragment = new Fragment("tabContentPanel", "comments", this);

                final CommentsListPanel comments = new CommentsListPanel("commentsList", new CommentsModel());
                comments.setOutputMarkupId(true);

                fragment.add(new CommentsPanel("addComment", getModel(), new Component[] { comments }));
                fragment.add(comments);
                break;
            case PHOTOS:
                // Create photos
                fragment = new Fragment("tabContentPanel", "photos", this);

                PhotoPanel photoPanel = new PhotoPanel("photos", getModel());
                fragment.add(photoPanel);
                break;
            case VIDEO:
                // Create video
                fragment = new Fragment("tabContentPanel", "video", this);
                fragment.add(new YouTubePanel("video", (IModel<Video>) () ->
                        getModel().getObject().getVideo()));
                break;
            default:
                throw new IllegalStateException("Invalid tab content type");
        }

        tabContent.addOrReplace(fragment);
    }

    protected IModel<Game> getModel() {
        return (IModel<Game>)getDefaultModel();
    }

    protected void addTabComponent() {
        tabNumberModel = new TabNumberModel(0);
        List<IModel> models = new ArrayList<IModel>();
        tabContentType = new Vector<TabContentType>();

        // Comments
        models.add(Model.of(getString("comments")));
        tabContentType.add(TabContentType.COMMENTS);

        // Photos
        Game g = getModel().getObject();
        if (((g.getPhotos() != null) && (!g.getPhotos().isEmpty())) || games.canEditGame(g)) {
            models.add(Model.of(getString("photos")));
            tabContentType.add(TabContentType.PHOTOS);
        }

        // Video
        if (g.getVideo() != null) {
            models.add(Model.of(getString("video")));
            tabContentType.add(TabContentType.VIDEO);
        }

        add(new TabsComponentPanel("tabs", tabNumberModel, models.toArray(new IModel[models.size()])));
    }

    @Override
    protected void onInitialize() {
        // Init this model ASAP, since parent needs it
        previewImageUrlModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                PageParameters pp = new PageParameters();
                pp.add(Images.RESOURCE_REFERENCE_ID_PARAM_NAME, getModel().getObject().getId());
                return urlFor(games.getIconReference(), pp).toString();
            }
        };

        super.onInitialize();

        add(new GameDetailPanel("gameDetail", getModel()));

        addTabComponent();

        // Tab content
        tabContent = new WebMarkupContainer("tabContent");
        tabContent.setOutputMarkupId(true);
        add(tabContent);
        addOrReplaceTabContentPanel();

        WebMarkupContainer ratingsContainerPanel = new WebMarkupContainer("ratingsContainerPanel");
        ratingsContainerPanel.setOutputMarkupId(true);

        // Prepare contact panel - we need it in ratings
        SendInformation contact = new SendInformation("contact", getModel());

        // Ratings result
        ratingsResult = new RatingsResultPanel("ratingsResults", getModel(), ratingsContainerPanel, contact.getWantedToPlay());
        ratingsResult.setOutputMarkupId(true);
        ratingsContainerPanel.add(ratingsResult);

        ratingsPanel = new RatingsPanel("ratingsPanel", getModel(), ratingsContainerPanel);
        ratingsContainerPanel.add(ratingsPanel);

        if (getModel().getObject().isRatingsDisabled()) {
            // Cannot rate because ratings are disabled
            ratingsContainerPanel.add(new RatingsDisabledPanel("canNotRatePanel"));
        }
        else {
            // Check whether user logged in and show appropriate message if applicable
            ratingsContainerPanel.add(new LoginToRatePanel("canNotRatePanel"));
        }

        addAuthorRatePanel(ratingsContainerPanel);

        add(ratingsContainerPanel);

        EditGamePanel editGamePanel = new EditGamePanel("editGamePanel", getModel());
        add(editGamePanel);

        DeleteGamePanel deleteGamePanel = new DeleteGamePanel("deleteGamePanel", getModel().getObject().getId());
        add(deleteGamePanel);

        add(new GameListPanel("similarGames", new LoadableDetachableModel<>() {
            @Override
            protected List<Game> load() {
                return similarGames.allForGame(getModel().getObject());
            }
        }));

        add(new GameListPanel("gamesOfAuthors", new LoadableDetachableModel<>() {
            @Override
            protected List<Game> load() {
                return authoredGames.gamesOfAuthors(getModel().getObject());
            }
        }));

        add(new EventsListLinksOnly("associatedEvents", new EventsModel(getModel().getObject().getId())));

        // Contact
        EnclosureContainer contactEnclosure = new EnclosureContainer("contactEnclosure", contact);
        contactEnclosure.add(contact);
        add(contactEnclosure);

        // Ratings of users for admin
        AdminAllRatingsPanel adminRatingsPanel = new AdminAllRatingsPanel("ratingsOfUsersPanel", getModel());
        EnclosureContainer ratingsOfUsersPanelEnclosure = new EnclosureContainer("ratingsOfUsersPanelEnclosure", adminRatingsPanel);
        ratingsOfUsersPanelEnclosure.add(adminRatingsPanel);
        add(ratingsOfUsersPanelEnclosure);

        if (appUsers.isSignedIn()) {
            add(new JSPingBehavior());
        }
    }

    /**
     * Add container for case when author wants to rate his/her own game
     */
    private void addAuthorRatePanel(final MarkupContainer container) {
        final Form<Void> authorRatePanel = new Form<Void>("authorRatePanel");
        container.add(authorRatePanel);
        container.setOutputMarkupId(true);

        // Check if current user is author of the game
        boolean showPanel = false;
        if (appUsers.isSignedIn() && !ratingsPanel.isRatingSet()) {
            Integer userId = appUsers.getLoggedUser().getId();
            for (CsldUser u : getModel().getObject().getAuthors()) {
                if (userId.equals(u.getId())) {
                    showPanel = true;
                    break;
                }
            }
        }

        if (!showPanel) {
            // No author - hide this panel
            authorRatePanel.setVisible(false);
            return;
        }

        // Show this panel - hide ratings panel
        ratingsPanel.setOutputMarkupId(true);
        ratingsPanel.setVisible(false);

        // Add button
        authorRatePanel.add(new AjaxButton("authorRate", new ResourceModel("Rating.author.button"), authorRatePanel) {
            @Override
            protected void onSubmit(AjaxRequestTarget art) {
                // Show ratings panel and hide this panel
                ratingsPanel.setVisible(true);
                authorRatePanel.setVisible(false);
                art.add(container);
            }
        });

    }

    public static PageParameters paramsForGame(Game game) {
        PageParameters pp = new PageParameters();

        if (game != null) {
            pp.add(ID_PARAM, game.getId());
            pp.add("name", Strings.removeAccents(game.getName()).toLowerCase().replaceAll("[^a-z0-9\\.]", "-").replaceAll("-+", "-").replaceAll("-$", ""));
        }

        return pp;
    }
}