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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Locale;

public class TranslateGame extends CsldBasePage {
    private static final String ID_PARAM = "id";
    private final static Logger logger = Logger.getLogger(GameDetail.class);
    private ListView<GameHasLanguages> translationsShow;
    private LanguagesModel languagesModel;

    @SpringBean
    GameService gameService;

    private class LanguagesModel extends LoadableDetachableModel<List<GameHasLanguages>> {
        // Game id. We could also store id as page property.
        private int gameId;

        private LanguagesModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected List<GameHasLanguages> load() {
            logger.debug("Loading languages for id "+gameId);

            Game game = gameService.getById(gameId);
            if(HbUtils.isProxy(game)){
                game = HbUtils.deproxy(game);
            }

            return game.getAvailableLanguages();
        }

        protected Game getGame(){
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
            languagesModel = new LanguagesModel(gameId);
            setDefaultModel(languagesModel);
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(ListGame.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        translationsShow = new ListView<GameHasLanguages>("translations", (IModel<List<GameHasLanguages>>) getDefaultModel()) {
            private TextField<String> name;
            private TextArea<String> description;
            private DropDownChoice<Language> actualLanguage;

            @Override
            protected void populateItem(ListItem<GameHasLanguages> item) {
                GameHasLanguages language = item.getModelObject();

                Form infoAboutLanguage = new Form<GameHasLanguages>("translation", new CompoundPropertyModel<GameHasLanguages>(language));
                infoAboutLanguage.add(name = new TextField<String>("name"));
                infoAboutLanguage.add(description = new TextArea<String>("description"));
                infoAboutLanguage.add(actualLanguage = new DropDownChoice<Language>("language",new CodeLocaleProvider().availableLanguages()));

                infoAboutLanguage.add(new AjaxButton("updateLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Game toModify = TranslateGame.this.languagesModel.getGame();
                        List<GameHasLanguages> allLangs = toModify.getAvailableLanguages();
                        boolean updatedLanguage = false;
                        for(GameHasLanguages lang: allLangs) {
                            if(lang.getLanguage().equals(actualLanguage.getConvertedInput())){
                                lang.setName(name.getConvertedInput());
                                lang.setDescription(description.getConvertedInput());
                                updatedLanguage = true;
                            }
                        }
                        if(!updatedLanguage) {
                            GameHasLanguages gameHasLanguages = new GameHasLanguages();
                            gameHasLanguages.setGame(toModify);
                            gameHasLanguages.setName(name.getConvertedInput());
                            gameHasLanguages.setDescription(description.getConvertedInput());
                            gameHasLanguages.setLanguage(actualLanguage.getConvertedInput());
                            toModify.getAvailableLanguages().add(gameHasLanguages);
                        }
                        gameService.saveOrUpdate(toModify);
                        target.add(TranslateGame.this);
                    }
                });

                infoAboutLanguage.add(new AjaxButton("removeLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Game toModify = TranslateGame.this.languagesModel.getGame();
                        List<GameHasLanguages> allLangs = toModify.getAvailableLanguages();
                        if(allLangs.size() <= 1 ) {
                            return;
                        }
                        GameHasLanguages toRemove = null;
                        for(GameHasLanguages lang: allLangs) {
                            if(lang.getLanguage().equals(actualLanguage.getConvertedInput())){
                                toRemove = lang;
                            }
                        }
                        if(toRemove != null) {
                            toModify.getAvailableLanguages().remove(toRemove);
                        }
                        gameService.deleteTranslation(toModify, actualLanguage.getConvertedInput());
                        target.add(TranslateGame.this);
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
                Game toUpdate = TranslateGame.this.languagesModel.getGame();
                language.setGame(toUpdate);
                toUpdate.getAvailableLanguages().add(language);
                target.add(TranslateGame.this);
            }
        });

        add(addLanguage);
    }
}
