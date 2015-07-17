package cz.larpovadatabaze.components.panel.news;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.common.JSPingBehavior;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.services.NewsService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cz.larpovadatabaze.lang.AvailableLanguages.availableLocaleNames;

/**
 * This panel can be used to produce new News. At the current stage it is just stored in the database and available
 * as news.
 */
public class CreateOrUpdateNewsPanel extends AbstractCsldPanel<News> {
    @SpringBean
    private NewsService news;

    public CreateOrUpdateNewsPanel(String id) {
        super(id);
    }

    public CreateOrUpdateNewsPanel(String id, IModel<News> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        News pieceOfNews = getModelObject();
        if(pieceOfNews == null) {
            pieceOfNews = new News();
        }

        /* Add Form to add News */
        final ValidatableForm<News> createOrUpdateNews = new ValidatableForm<News>("addNews", new CompoundPropertyModel<News>(pieceOfNews));
        createOrUpdateNews.setOutputMarkupId(true);
        add(createOrUpdateNews);

        /* Add editor to decide text of the piece of News. */
        WebMarkupContainer descriptionWrapper = new WebMarkupContainer("newsWrapper");
        createOrUpdateNews.add(descriptionWrapper);
        TextArea description = (TextArea) new TextArea<String>("text").setRequired(true);
        descriptionWrapper.add(description);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("newsFeedback", description, descriptionWrapper, "form.news.textHint"));

        List<String> availableLanguages = new ArrayList<String>(availableLocaleNames());
        final DropDownChoice<String> lang = new DropDownChoice<String>("lang", availableLanguages);
        createOrUpdateNews.add(lang);
        createOrUpdateNews.add(new CsldFeedbackMessageLabel("langFeedback", lang, null));

        /* Add button to create news piece. */
        createOrUpdateNews.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if (createOrUpdateNews.isValid()) {
                    News pieceOfNews = createOrUpdateNews.getModelObject();
                    if (news.saveOrUpdate(pieceOfNews)) {
                        onCsldAction(target, form);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);

                target.add(form);
            }
        });

        if (UserUtils.isSignedIn()) {
            add(new JSPingBehavior());
        }
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
