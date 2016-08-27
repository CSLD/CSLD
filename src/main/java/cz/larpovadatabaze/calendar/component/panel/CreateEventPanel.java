package cz.larpovadatabaze.calendar.component.panel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.datepicker.DatePicker;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.common.multiac.IMultiAutoCompleteSource;
import cz.larpovadatabaze.components.common.multiac.MultiAutoCompleteComponent;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.game.ChooseLabelsPanel;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.validator.AtLeastOneRequiredLabelValidator;
import cz.larpovadatabaze.validator.NonEmptyAuthorsValidator;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import java.util.Collection;
import java.util.Date;
import java.util.List;

abstract public class CreateEventPanel extends AbstractCsldPanel<Event> {
    private static final int AUTOCOMPLETE_CHOICES = 10;

    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private GameService gameService;

    public CreateEventPanel(String id, IModel<Event> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm<Event> createEvent = new ValidatableForm<>("addEvent", new CompoundPropertyModel<>(getModel()));
        createEvent.setOutputMarkupId(true);

        RequiredTextField name = new RequiredTextField<String>("name");
        createEvent.add(name);
        createEvent.add(new CsldFeedbackMessageLabel("nameFeedback", name, "form.game.nameHint"));

        FormComponent<Date> from = new DatePicker("from", "MM/dd/yyyy", new Options()).setRequired(true);
        createEvent.add(from);
        createEvent.add(new CsldFeedbackMessageLabel("fromFeedback", from, "form.event.fromHint"));

        FormComponent<Date> to = new DatePicker("to", "MM/dd/yyyy", new Options()).setRequired(true);
        createEvent.add(to);
        createEvent.add(new CsldFeedbackMessageLabel("toFeedback", to, "form.event.toHint"));

        RequiredTextField amountOfPlayers = new RequiredTextField<Integer>("amountOfPlayers");
        createEvent.add(amountOfPlayers);
        createEvent.add(new CsldFeedbackMessageLabel("amountOfPlayersFeedback", amountOfPlayers, "form.event.amountOfPlayersHint"));

        RequiredTextField location = new RequiredTextField<String>("loc");
        createEvent.add(location);
        createEvent.add(new CsldFeedbackMessageLabel("locFeedback", location, "form.event.locationHint"));

        WebMarkupContainer gamesWrapper = new WebMarkupContainer("gamesWrapper");
        createEvent.add(gamesWrapper);

        // Choose from games to associate event with.
        addGamesInput(gamesWrapper);
        // Create new game to associate with this event.
        addCreateGameButton(gamesWrapper);


        WebMarkupContainer descriptionWrapper = new WebMarkupContainer("descriptionWrapper");
        createEvent.add(descriptionWrapper);
        TextArea description = (TextArea) new TextArea<String>("description").setRequired(true);
        description.add(new CSLDTinyMceBehavior());
        descriptionWrapper.add(description);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("descriptionFeedback", description, descriptionWrapper, "form.game.descriptionHint"));

        ChooseLabelsPanel chooseLabels = new ChooseLabelsPanel("labels", new IModel<List<Label>>() {
            @Override
            public List<Label> getObject() {
                return createEvent.getModelObject().getLabels();
            }

            @Override
            public void setObject(List<Label> object) {
                createEvent.getModelObject().setLabels(object);
            }

            @Override
            public void detach() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        chooseLabels.setOutputMarkupId(true);
        chooseLabels.add(new AtLeastOneRequiredLabelValidator());
        createEvent.add(chooseLabels);
        WebMarkupContainer labelsFeedbackWrapper = new WebMarkupContainer("labelsFeedbackWrapper");
        createEvent.add(labelsFeedbackWrapper);
        labelsFeedbackWrapper.add(new CsldFeedbackMessageLabel("labelsFeedback", chooseLabels, labelsFeedbackWrapper, null));

        add(createEvent);

        createEvent.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                Event event = (Event) form.getModelObject();
                new DatabaseEvents(sessionFactory.getCurrentSession()).store(event);

                onCsldAction(target, form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if(!createEvent.isValid()){
                    target.add(getParent());
                }
            }
        }.add(new TinyMceAjaxSubmitModifier()));
    }

    private void addGamesInput(WebMarkupContainer gamesWrapper) {
        MultiAutoCompleteComponent<Game> associatedGames = new MultiAutoCompleteComponent<>("games", new PropertyModel<>(getModelObject(), "games"), new IMultiAutoCompleteSource<Game>() {
            @Override
            public Collection<Game> getChoices(String input) {
                return gameService.getFirstChoices(input, AUTOCOMPLETE_CHOICES);
            }

            @Override
            public Game getObjectById(Long id) {
                return gameService.getById(id.intValue());
            }
        });

        gamesWrapper.add(associatedGames);

        gamesWrapper.add(new CsldFeedbackMessageLabel("gamesFeedback", associatedGames, gamesWrapper, null));
    }

    private void addCreateGameButton(WebMarkupContainer gamesWrapper) {
        final ModalWindow createGameModal;
        add(createGameModal = new ModalWindow("createGame"));

        createGameModal.setTitle("Vytvořit hru");
        createGameModal.setCookieName("create-game");

        gamesWrapper.add(new AjaxButton("createGameBtn"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                createGameModal.setContent(new CreateOrUpdateGamePanel(createGameModal.getContentId(), Model.of(Game.getEmptyGame())){
                    @Override
                    protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                        super.onCsldAction(target, form);
                        createGameModal.close(target);
                    }
                });
                createGameModal.show(target);
            }
        }));
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
