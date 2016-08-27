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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

        add(filterGames);
    }

    /**
     * @return Wrapper for required labels section. The
     */
    public Component getRequiredLabelsWrapper() {
        return requiredLabels;
    }
}
