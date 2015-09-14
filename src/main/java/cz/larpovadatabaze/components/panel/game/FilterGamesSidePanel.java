package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Locale;

import static cz.larpovadatabaze.lang.AvailableLanguages.availableLocale;

/**
 * It contains basic characteristics of the game and allows user to filter by them.
 */
public class FilterGamesSidePanel extends AbstractCsldPanel<FilterGame> {

    @SpringBean
    private LabelService labelService;

    private FilterByLabelsPanel requiredLabels;

    public FilterGamesSidePanel(String id, IModel<FilterGame> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm<FilterGame> filterGames =
                new ValidatableForm<FilterGame>("filterForm", new CompoundPropertyModel<FilterGame>(getModel())){};


        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();

        CheckBox showArchived = new CheckBox("showArchived");
        filterGames.add(showArchived);
        showArchived.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ((ListGamePage) getPage()).filterChanged(false, false, false);
            }
        });

        requiredLabels = new FilterByLabelsPanel("requiredLabels", labelService.getAuthorizedRequired(logged), true);
        requiredLabels.setOutputMarkupId(true);
        filterGames.add(requiredLabels);

        filterGames.add(new FilterByLabelsPanel("otherLabels", labelService.getAuthorizedOptional(logged), false));

        // Languages
        filterGames.add(new ListView<Locale>("languages", availableLocale()) {
            @Override
            protected void populateItem(ListItem<Locale> item) {
                final Locale ourLocale = item.getModelObject();

                // Add checkbox
                CheckBox cb = new CheckBox("checkbox", new IModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return filterGames.getModelObject().getLanguages().contains(ourLocale);
                    }

                    @Override
                    public void setObject(Boolean object) {
                        if (Boolean.TRUE.equals(object)) {
                            if (!filterGames.getModelObject().getLanguages().contains(ourLocale)) {
                                filterGames.getModelObject().getLanguages().add(ourLocale);
                            }
                        }
                        else {
                            filterGames.getModelObject().getLanguages().remove(ourLocale);
                        }
                    }

                    @Override
                    public void detach() {
                        // Nothing to do
                    }
                });
                item.add(cb);
                cb.add(new AjaxFormComponentUpdatingBehavior("change") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        ((ListGamePage)(getPage())).filterChanged(false, false, false);
                    }
                });

                item.add(new Label("label", item.getModelObject().getLanguage()));
            }
        });

        add(filterGames);
    }

    /**
     * @return Wrapper for required labels section. The
     */
    public Component getRequiredLabelsWrapper() {
        return requiredLabels;
    }
}
