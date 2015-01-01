package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.List;
import java.util.Locale;

/**
 * It contains basic characteristics of the game and allows user to filter by them.
 */
public abstract class FilterGamesPanel extends Panel {
    public FilterGamesPanel(String id) {
        super(id);

        final ValidatableForm<FilterGame> filterGames =
                new ValidatableForm<FilterGame>("filterForm", new CompoundPropertyModel<FilterGame>(new FilterGame())){};

        filterGames.add(new NumberTextField<Double>("minPlayers"));
        filterGames.add(new NumberTextField<Double>("maxPlayers"));
        filterGames.add(new NumberTextField<Double>("minHours"));
        filterGames.add(new NumberTextField<Double>("maxHours"));
        filterGames.add(new NumberTextField<Double>("minDays"));
        filterGames.add(new NumberTextField<Double>("maxDays"));

        ChooseLabelsPanel labels = new ChooseLabelsPanel("filterByLabels");
        filterGames.add(labels);

        List<Locale> locales = new CodeLocaleProvider().availableLocale();
        if(filterGames.getModelObject().getLanguage() == null) {
            filterGames.getModelObject().setLanguage(Session.get().getLocale());
        }
        final DropDownChoice<Locale> changeLocale =
                new DropDownChoice<Locale>("language", locales);
        filterGames.add(changeLocale);

        filterGames.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if(filterGames.isValid()){
                    onCsldEvent(target, form, ((FilterGame)form.getModelObject()).getFilterByLabels());
                }
            }
        });

        add(filterGames);
    }

    public void onCsldEvent(AjaxRequestTarget target, Form<?> form, List<Label> labels){}
}
