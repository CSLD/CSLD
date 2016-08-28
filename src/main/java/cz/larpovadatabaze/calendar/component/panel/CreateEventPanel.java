package cz.larpovadatabaze.calendar.component.panel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.datepicker.DatePicker;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.calendar.Location;
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
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.validator.AtLeastOneRequiredLabelValidator;
import cz.larpovadatabaze.validator.NonEmptyAuthorsValidator;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.GMapHeaderContributor;
import org.wicketstuff.gmap.api.GLatLng;
import org.wicketstuff.gmap.api.GMarker;
import org.wicketstuff.gmap.api.GMarkerOptions;
import org.wicketstuff.gmap.event.ClickListener;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

abstract public class CreateEventPanel extends AbstractCsldPanel<Event> {
    private static final int AUTOCOMPLETE_CHOICES = 10;

    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private GameService gameService;

    private GLatLng lastSelectedLocation;

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

        // Web
        TextField<String> web = new TextField<String>("web");
        createEvent.add(web);
        createEvent.add(new CsldFeedbackMessageLabel("webFeedback", web, "form.game.webHint"));

        addMap(createEvent, ((Event) getDefaultModelObject()).getLocation());

        add(createEvent);

        createEvent.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                Event event = (Event) form.getModelObject();
                if(lastSelectedLocation != null) {
                    event.setLocation(new Location(lastSelectedLocation.getLat(), lastSelectedLocation.getLng()));
                }
                if(event.getAddedBy() == null) {
                    event.setAddedBy(CsldAuthenticatedWebSession.get().getLoggedUser());
                }
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

    private void addMap(Form container, Location location){
        GMap map = new GMap("map", new GMapHeaderContributor("http", "AIzaSyC8K3jrJMl52-Mswi2BsS5UVKDZIT4GWh8")); // TODO: Restrict usage of the key.
        map.setStreetViewControlEnabled(false);
        map.setScaleControlEnabled(true);
        map.setScrollWheelZoomEnabled(true);
        map.setCenter(new GLatLng(52.47649, 13.228573));

        map.add(new ClickListener()
        {
            @Override
            protected void onClick(AjaxRequestTarget target, GLatLng latLng) {
                if (latLng != null) {
                    lastSelectedLocation = latLng;

                    map.removeAllOverlays();
                    map.addOverlay(new GMarker(new GMarkerOptions(map, latLng)));
                }
            }
        });

        if(location != null) {
            lastSelectedLocation = new GLatLng(location.getLatitude(), location.getLongitude());
            map.addOverlay(new GMarker(new GMarkerOptions(map, lastSelectedLocation)));
        }

        container.add(map);
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
