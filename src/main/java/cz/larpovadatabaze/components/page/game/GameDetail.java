package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.common.tabs.TabsComponentPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.YouTubePanel;
import cz.larpovadatabaze.components.panel.admin.AdminAllRatingsPanel;
import cz.larpovadatabaze.components.panel.game.*;
import cz.larpovadatabaze.components.panel.photo.PhotoPanel;
import cz.larpovadatabaze.components.panel.user.SimpleListUsersPanel;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.HbUtils;
import cz.larpovadatabaze.utils.Strings;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
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

    private static enum TabContentType { COMMENTS, PHOTOS, VIDEO };

    @SpringBean
    GameService gameService;

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
                RequestCycle.get().find(AjaxRequestTarget.class).add(tabContent);
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

            Game game = gameService.getById(gameId);
            if(HbUtils.isProxy(game)){
            }    game = HbUtils.deproxy(game);


            return game;
        }
    }

    /**
     * Model for comments of actual game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     */
    private class CommentsModel extends LoadableDetachableModel<List<Comment>> {
        @Override
        public List<Comment> load() {
            List<Comment> res = new ArrayList<Comment>();

            // Fill in array
            if (UserUtils.isEditor()) {
                // Editors see everything
                res.addAll(getModel().getObject().getComments());
            }
            else {
                // Filter
                Integer thisUserId = null;
                CsldUser user = UserUtils.getLoggedUser();
                if (user != null) {
                    thisUserId = user.getId();
                }
                for(Comment c : getModel().getObject().getComments()) {
                    if (c.getHidden()) {
                        if (!c.getUserId().equals(thisUserId)) continue; // Hidden comment and user is not creator - hide
                    }
                    res.add(c);
                }
            }

            // Sort
            Collections.sort(res, new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return -o1.getAdded().compareTo(o2.getAdded());
                }
            });

            return res;
        }
    }

    /**
     * Model for users who want to play the game. (Might get GameModel as constructor parameter to be extra clean, but we use the one stored in the page.)
     * The downside is it does not cache results so getObject() may be costly.
     */
    private class WantedByModel extends AbstractReadOnlyModel<List<CsldUser>> {

        @Override
        public List<CsldUser> getObject() {
            List<CsldUser> wantedBy = new ArrayList<CsldUser>();
            for(UserPlayedGame played : getModel().getObject().getPlayed()){
                if(played.getStateEnum().equals(UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY)) {
                    wantedBy.add(played.getPlayerOfGame());
                }
            }

            return wantedBy;
        }
    }

    /**
     * Constructor - initialize just model
     */
    public GameDetail(PageParameters params){
        try {
        setDefaultModel(new GameModel(params.get(ID_PARAM).to(Integer.class)));
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(HomePage.class);
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
                fragment.add(new YouTubePanel("video", new AbstractReadOnlyModel<Video>() {
                    @Override
                    public Video getObject() {
                        return getModel().getObject().getVideo();
                    }
                }));
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
        models.add(Model.of("Komentáře"));
        tabContentType.add(TabContentType.COMMENTS);

        // Photos
        Game g = getModel().getObject();
        if (((g.getPhotos() != null) && (!g.getPhotos().isEmpty())) || gameService.canEditGame(g)) {
            models.add(Model.of("Fotky"));
            tabContentType.add(TabContentType.PHOTOS);
        }

        // Video
        if (g.getVideo() != null) {
            models.add(Model.of("Video"));
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
                pp.add(ImageService.RESOURCE_REFERENCE_ID_PARAM_NAME, getModel().getObject().getId());
                return urlFor(gameService.getIconReference(), pp).toString();
            }
        };

        super.onInitialize();

        final SimpleListUsersPanel wantedToPlay =  new SimpleListUsersPanel("wantsToPlay", new WantedByModel());
        wantedToPlay.setOutputMarkupId(true);

        add(new GameDetailPanel("gameDetail", getModel()));

        addTabComponent();

        // Tab content
        tabContent = new WebMarkupContainer("tabContent");
        tabContent.setOutputMarkupId(true);
        add(tabContent);
        addOrReplaceTabContentPanel();

        WebMarkupContainer ratingsContainerPanel = new WebMarkupContainer("ratingsContainerPanel");
        ratingsContainerPanel.setOutputMarkupId(true);

        ratingsResult = new RatingsResultPanel("ratingsResults", getModel(), ratingsContainerPanel, wantedToPlay);
        ratingsResult.setOutputMarkupId(true);
        ratingsContainerPanel.add(ratingsResult);

        ratingsPanel = new RatingsPanel("ratingsPanel", getModel(), ratingsContainerPanel);
        ratingsContainerPanel.add(ratingsPanel);

        ratingsContainerPanel.add(new CanNotRatePanel("canNotRatePanel"));

        addAuthorRatePanel(ratingsContainerPanel);

        add(ratingsContainerPanel);

        EditGamePanel editGamePanel = new EditGamePanel("editGamePanel", getModel());
        add(editGamePanel);

        DeleteGamePanel deleteGamePanel = new DeleteGamePanel("deleteGamePanel", getModel().getObject().getId());
        add(deleteGamePanel);

        add(new GameListPanel("similarGames", new LoadableDetachableModel<List<? extends Game>>() {
            @Override
            protected List<Game> load() {
                return gameService.getSimilar(getModel().getObject());
            }
        }));

        add(new GameListPanel("gamesOfAuthors", new LoadableDetachableModel<List<? extends Game>>() {
            @Override
            protected List<Game> load() {
                return gameService.gamesOfAuthors(getModel().getObject());
            }
        }));

        add(new AdminAllRatingsPanel("ratingsOfUsersPanel", getModel()));

        add(wantedToPlay);
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
        if (UserUtils.isSignedIn() && !ratingsPanel.isRatingSet()) {
            Integer userId = UserUtils.getLoggedUser().getId();
            for(CsldUser u : getModel().getObject().getAuthors()) {
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
            protected void onSubmit(AjaxRequestTarget art, Form<?> form) {
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
