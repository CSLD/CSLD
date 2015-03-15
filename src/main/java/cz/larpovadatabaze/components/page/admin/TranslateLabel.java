package cz.larpovadatabaze.components.page.admin;

import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.entities.LabelHasLanguages;
import cz.larpovadatabaze.entities.Language;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.LabelService;
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

public class TranslateLabel extends CsldBasePage {
    public static final String ID_PARAM = "id";
    private final static Logger logger = Logger.getLogger(TranslateLabel.class);
    private ListView<LabelHasLanguages> translationsShow;
    private LanguagesModel languagesModel;

    @SpringBean
    LabelService labelService;

    private class LanguagesModel extends LoadableDetachableModel<cz.larpovadatabaze.entities.Label> {
        // Game id. We could also store id as page property.
        private int gameId;

        private LanguagesModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected cz.larpovadatabaze.entities.Label load() {
            logger.debug("Loading languages for id "+gameId);

            cz.larpovadatabaze.entities.Label label = labelService.getById(gameId);
            if(HbUtils.isProxy(label)){
                label = HbUtils.deproxy(label);
            }

            return label;
        }
    }

    public TranslateLabel(PageParameters params) {
        try {
            int labelId = params.get(ID_PARAM).to(Integer.class);
            languagesModel = new LanguagesModel(labelId);
            setDefaultModel(new CompoundPropertyModel<Object>(languagesModel));
        } catch (NumberFormatException ex) {
            throw new RestartResponseException(HomePage.class);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        translationsShow = new ListView<LabelHasLanguages>("labelHasLanguages", languagesModel.load().getLabelHasLanguages()) {
            @Override
            protected void populateItem(final ListItem<LabelHasLanguages> item) {
                LabelHasLanguages language = item.getModelObject();

                Form infoAboutLanguage = new Form<LabelHasLanguages>("translation", new CompoundPropertyModel<LabelHasLanguages>(language));
                infoAboutLanguage.add(new TextField<String>("name"));
                infoAboutLanguage.add(new TextArea<String>("description").add(new CSLDTinyMceBehavior()));
                infoAboutLanguage.add(new Label("language"));

                infoAboutLanguage.add(new AjaxSubmitLink("updateLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        cz.larpovadatabaze.entities.Label toModify = TranslateLabel.this.languagesModel.getObject();
                        LabelHasLanguages langToSave = (LabelHasLanguages) getForm().getModelObject();
                        boolean wasUpdate = false;
                        for(LabelHasLanguages lang: toModify.getLabelHasLanguages()){
                            if(lang.getLanguage().equals(langToSave.getLanguage())){
                                lang.setDescription(langToSave.getDescription());
                                lang.setName(langToSave.getName());
                                wasUpdate = true;
                            }
                        }
                        if(!wasUpdate) {
                            toModify.getLabelHasLanguages().add(langToSave);
                        }
                        labelService.saveOrUpdate(toModify);
                        target.add(TranslateLabel.this);
                    }
                }.add(new TinyMceAjaxSubmitModifier()));

                infoAboutLanguage.add(new AjaxButton("removeLanguage") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        cz.larpovadatabaze.entities.Label toModify = TranslateLabel.this.languagesModel.getObject();
                        List<LabelHasLanguages> allLangs = toModify.getLabelHasLanguages();
                        if(allLangs.size() <= 1 ) {
                            return;
                        }
                        LabelHasLanguages toRemove = item.getModel().getObject();
                        translationsShow.getModelObject().remove(toRemove);
                        for(LabelHasLanguages lang: toModify.getLabelHasLanguages()){
                            if(lang.getLanguage().equals(toRemove.getLanguage())){
                                toRemove = lang;
                            }
                        }
                        toModify.getLabelHasLanguages().remove(toRemove);
                        labelService.deleteTranslation(toRemove);
                        translationsShow.setModelObject(toModify.getLabelHasLanguages());
                        translationsShow.removeAll();
                        target.add(TranslateLabel.this);
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
                LabelHasLanguages language = new LabelHasLanguages();
                cz.larpovadatabaze.entities.Label toUpdate = TranslateLabel.this.languagesModel.getObject();
                language.setName("");
                language.setDescription("");
                language.setLabel(toUpdate);
                language.setLanguage(actualLang.getConvertedInput());
                for(LabelHasLanguages lang: toUpdate.getLabelHasLanguages()){
                    if(lang.getLanguage().equals(language.getLanguage())){
                        return;
                    }
                }
                translationsShow.getModelObject().add(language);
                //toUpdate.getAvailableLanguages().add(language);
                //gameService.saveOrUpdate(toUpdate);
                target.add(TranslateLabel.this);
            }
        });

        add(addLanguage);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(TranslateLabel.class, "TranslateLabel.css")));

        super.renderHead(response);
    }
}
