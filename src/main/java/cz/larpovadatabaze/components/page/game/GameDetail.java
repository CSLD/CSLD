package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.common.tabs.TabsComponentPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.*;
import cz.larpovadatabaze.components.panel.photo.PhotoPanel;
import cz.larpovadatabaze.components.panel.user.SimpleListUsersPanel;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.models.ReadOnlyModel;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class GameDetail extends CsldBasePage {
    @SpringBean
    GameService gameService;

    private RatingsResultPanel ratingsResult;
    private RatingsPanel ratingsPanel;

    private TabNumberModel tabNumberModel;

    private WebMarkupContainer tabContent;

    private final static Logger logger = Logger.getLogger(GameDetail.class);

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
    private class CommentsModel extends ReadOnlyModel<List<Comment>> {
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
    private class WantedByModel extends ReadOnlyModel<List<CsldUser>> {

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

        if (tabNumberModel.getObject() == 0) {
            // Create comments
            fragment = new Fragment("tabContentPanel", "comments", this);

            final CommentsListPanel comments = new CommentsListPanel("commentsList", new CommentsModel());
            comments.setOutputMarkupId(true);

            fragment.add(new CommentsPanel("addComment", getModel(), new Component[] { comments }));
            fragment.add(comments);
        }
        else {
            // Create photos
            fragment = new Fragment("tabContentPanel", "photos", this);

            PhotoPanel photoPanel = new PhotoPanel("photos", getModel());
            fragment.add(photoPanel);

        }

        tabContent.addOrReplace(fragment);
    }

    protected IModel<Game> getModel() {
        return (IModel<Game>)getDefaultModel();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final SimpleListUsersPanel wantedToPlay =  new SimpleListUsersPanel("wantsToPlay", new WantedByModel());
        wantedToPlay.setOutputMarkupId(true);

        add(new GameDetailPanel("gameDetail", getModel()));

        // Tabs
        tabNumberModel = new TabNumberModel(0);
        add(new TabsComponentPanel("tabs", tabNumberModel, new IModel[] { Model.of("Komentáře"), Model.of("Fotky") }));

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
