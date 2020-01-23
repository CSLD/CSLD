package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.ValidatableForm;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.games.components.page.ListGamePage;
import cz.larpovadatabaze.games.models.FilterGame;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * It contains basic characteristics of the game and allows user to filter by them.
 */
public class FilterGamesSidePanel extends AbstractCsldPanel<FilterGame> {

    @SpringBean
    private Labels labels;

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

        requiredLabels = new FilterByLabelsPanel("requiredLabels", labels.getAuthorizedRequired(logged), true);
        requiredLabels.setOutputMarkupId(true);
        filterGames.add(requiredLabels);

        filterGames.add(new FilterByLabelsPanel("otherLabels", labels.getAuthorizedOptional(logged), false));

        add(filterGames);
    }

    /**
     * @return Wrapper for required labels section. The
     */
    public Component getRequiredLabelsWrapper() {
        return requiredLabels;
    }
}
