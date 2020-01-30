package cz.larpovadatabaze.calendar.component.panel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.datepicker.AjaxDatePicker;
import cz.larpovadatabaze.calendar.Area;
import cz.larpovadatabaze.calendar.model.FilterEvent;
import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.FilterablePage;
import cz.larpovadatabaze.common.components.ValidatableForm;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.games.components.panel.FilterByLabelsPanel;
import cz.larpovadatabaze.games.services.Labels;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collection;


/**
 * It contains basic characteristics of the game and allows user to filter by them.
 */
public class FilterEventsSidePanel extends AbstractCsldPanel<FilterEvent> {

    @SpringBean
    private Labels labels;

    private FilterByLabelsPanel requiredLabels;

    public FilterEventsSidePanel(String id, IModel<FilterEvent> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm<FilterEvent> filterGames =
                new ValidatableForm<FilterEvent>("filterForm", new CompoundPropertyModel<>(getModel())){};


        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();

        filterGames.add(new AjaxDatePicker("from", "dd.MM.yyyy", new Options()){

            @Override
            public void onValueChanged(IPartialPageRequestHandler handler) {
                ((FilterablePage)getPage()).filterChanged(false, false, false);
            }
        });
        filterGames.add(new AjaxDatePicker("to", "dd.MM.yyyy", new Options()){
            @Override
            public void onValueChanged(IPartialPageRequestHandler handler) {
                ((FilterablePage)getPage()).filterChanged(false, false, false);
            }
        });

        Collection<Area> areas = getModelObject().getFilter().areas();
        ChoiceRenderer<Area> choiceRenderer = new ChoiceRenderer<>("area", "area");
        filterGames.add(new DropDownChoice<>("region", new ArrayList<>(areas), choiceRenderer).setOutputMarkupId(true).add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ((FilterablePage)getPage()).filterChanged(false, false, false);
            }
        }));

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
