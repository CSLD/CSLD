package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 *
 */
public abstract class FilterGamesPanel extends Panel {
    private ChooseLabelsPanel labels;

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

        labels = new ChooseLabelsPanel("filterByLabels");
        filterGames.add(labels);

        filterGames.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if(filterGames.isValid()){
                    List<Label> selectedLabels = labels.getSelected();

                    onCsldEvent(target, form, selectedLabels);
                }
            }
        });

        add(filterGames);
    }

    public void onCsldEvent(AjaxRequestTarget target, Form<?> form, List<Label> labels){}
}
