package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.entities.Language;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import java.util.List;
import java.util.Locale;

public class TranslateGame extends CsldBasePage {
    private static final String ID_PARAM = "id";
    private final static Logger logger = Logger.getLogger(TranslateGame.class);
    private ListView<GameHasLanguages> translationsShow;
    private LanguagesModel languagesModel;

    @SpringBean
    GameService gameService;

    private class LanguagesModel extends LoadableDetachableModel<Game> {
        // Game id. We could also store id as page property.
        private int gameId;

        private LanguagesModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected Game load() {
            logger.debug("Loading languages for id "+gameId);

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
            setDefaultModel(new CompoundPropertyModel<Object>(languagesModel));
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(ListGame.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        translationsShow = new ListView<GameHasLanguages>("availableLanguages", languagesModel.load().getAvailableLanguages()) {
            @Override
            protected void populateItem(final ListItem<GameHasLanguages> item) {
                GameHasLanguages language = item.getModelObject();

                Form infoAboutLanguage = new Form<GameHasLanguages>("translation", new CompoundPropertyModel<GameHasLanguages>(language));
                infoAboutLanguage.add(new TextField<String>("name"));
                infoAboutLanguage.add(new TextArea<String>("description").add(new CSLDTinyMceBehavior()));
                infoAboutLanguage.add(new Label("language"));

                infoAboutLanguage.add(new AjaxSubmitLink("updateLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Game toModify = TranslateGame.this.languagesModel.getObject();
                        GameHasLanguages langToSave = (GameHasLanguages) getForm().getModelObject();
                        boolean wasUpdate = false;
                        for(GameHasLanguages lang: toModify.getAvailableLanguages()){
                            if(lang.getLanguage().equals(langToSave.getLanguage())){
                                lang.setDescription(langToSave.getDescription());
                                lang.setName(langToSave.getName());
                                wasUpdate = true;
                            }
                        }
                        if(!wasUpdate) {
                            toModify.getAvailableLanguages().add(langToSave);
                        }
                        gameService.saveOrUpdate(toModify);
                        target.add(TranslateGame.this);
                    }
                }.add(new TinyMceAjaxSubmitModifier()));

                infoAboutLanguage.add(new AjaxButton("removeLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        Game toModify = TranslateGame.this.languagesModel.getObject();
                        List<GameHasLanguages> allLangs = toModify.getAvailableLanguages();
                        if(allLangs.size() <= 1 ) {
                            return;
                        }
                        GameHasLanguages toRemove = item.getModel().getObject();
                        translationsShow.getModelObject().remove(toRemove);
                        for(GameHasLanguages lang: toModify.getAvailableLanguages()){
                            if(lang.getLanguage().equals(toRemove.getLanguage())){
                                toRemove = lang;
                            }
                        }
                        toModify.getAvailableLanguages().remove(toRemove);
                        gameService.deleteTranslation(toRemove);
                        translationsShow.setModelObject(toModify.getAvailableLanguages());
                        translationsShow.removeAll();
                        target.add(TranslateGame.this);
                    }
                });

                item.add(infoAboutLanguage);
            }
        };
        translationsShow.setOutputMarkupId(true);
        translationsShow.setReuseItems(true);
        add(translationsShow);

        Form addLanguage = new Form("addLanguage");
        final DropDownChoice<Language> actualLang = new DropDownChoice<Language>("language", Model.of(new Language()), new CodeLocaleProvider().availableLanguages());
        addLanguage.add(actualLang);
        addLanguage.add(new AjaxButton("addAnotherLanguage") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                GameHasLanguages language = new GameHasLanguages();
                Game toUpdate = TranslateGame.this.languagesModel.getObject();
                language.setName("");
                language.setDescription("");
                language.setGame(toUpdate);
                language.setLanguage(actualLang.getConvertedInput());
                for(GameHasLanguages lang: toUpdate.getAvailableLanguages()){
                    if(lang.getLanguage().equals(language.getLanguage())){
                        return;
                    }
                }
                translationsShow.getModelObject().add(language);
                //toUpdate.getAvailableLanguages().add(language);
                //labelService.saveOrUpdate(toUpdate);
                target.add(TranslateGame.this);
            }
        });

        add(addLanguage);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(TranslateGame.class, "TranslateGame.css")));

        super.renderHead(response);
    }
}
