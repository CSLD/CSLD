package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.entities.Language;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Locale;

public class TranslateGame extends CsldBasePage {
    private static final String ID_PARAM = "id";
    private final static Logger logger = Logger.getLogger(GameDetail.class);
    private ListView<GameHasLanguages> translationsShow;

    @SpringBean
    GameService gameService;

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
                game = HbUtils.deproxy(game);
            }

            return game;
        }
    }

    public TranslateGame(PageParameters params) {
        try {
            int gameId = params.get(ID_PARAM).to(Integer.class);
            setDefaultModel(new GameModel(gameId));
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(ListGame.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        translationsShow = new ListView<GameHasLanguages>("translations", ((Game) getDefaultModel().getObject()).getAvailableLanguages()) {
            @Override
            protected void populateItem(ListItem<GameHasLanguages> item) {
                GameHasLanguages language = item.getModelObject();

                Form infoAboutLanguage = new Form<GameHasLanguages>("translation", new CompoundPropertyModel<GameHasLanguages>(language));
                infoAboutLanguage.add(new TextField<String>("name"));
                infoAboutLanguage.add(new TextArea<String>("description"));
                infoAboutLanguage.add(new DropDownChoice<Language>("languageForGame",new CodeLocaleProvider().availableLanguages()));

                infoAboutLanguage.add(new AjaxButton("updateLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Game toModify = ((Game) TranslateGame.this.getDefaultModel().getObject());
                        gameService.saveOrUpdate(toModify);
                    }
                });

                infoAboutLanguage.add(new AjaxButton("removeLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Game toModify = ((Game) TranslateGame.this.getDefaultModel().getObject());
                        gameService.saveOrUpdate(toModify);
                    }
                });

                item.add(infoAboutLanguage);
            }
        };
        translationsShow.setOutputMarkupId(true);
        add(translationsShow);

        Form addLanguage = new Form("addLanguage");

        addLanguage.add(new AjaxButton("addAnotherLanguage") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                GameHasLanguages language = new GameHasLanguages();
                Game toUpdate = ((Game) TranslateGame.this.getDefaultModel().getObject());
                language.setGame(toUpdate);
                toUpdate.getAvailableLanguages().add(language);
                target.add(TranslateGame.this);
            }
        });

        add(addLanguage);
    }
}
