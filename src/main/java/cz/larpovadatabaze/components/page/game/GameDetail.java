package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.common.tabs.TabsComponentPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.YouTubePanel;
import cz.larpovadatabaze.components.panel.game.*;
import cz.larpovadatabaze.components.panel.photo.PhotoPanel;
import cz.larpovadatabaze.components.panel.user.SimpleListUsersPanel;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
    private static enum TabContentType { COMMENTS, PHOTOS, VIDEO };

    @SpringBean
    GameService gameService;

    private RatingsResultPanel ratingsResult;
    private RatingsPanel ratingsPanel;

    private TabNumberModel tabNumberModel;

    private WebMarkupContainer tabContent;

    private final static Logger logger = Logger.getLogger(GameDetail.class);

    private Vector<TabContentType> tabContentType;

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
    private class CommentsModel extends AbstractReadOnlyModel<List<Comment>> {
        @Override
        public List<Comment> getObject() {
            List<Comment> res = new ArrayList<Comment>(getModel().getObject().getComments());
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
        setDefaultModel(new GameModel(params.get("id").to(Integer.class)));
    }

    @Override
    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("larpDatabaseTitleForGame", getDefaultModel());
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

        add(wantedToPlay);
    }
}
