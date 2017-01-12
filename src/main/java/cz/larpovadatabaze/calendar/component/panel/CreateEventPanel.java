package cz.larpovadatabaze.calendar.component.panel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.datepicker.DatePicker;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.calendar.Location;
import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.component.validator.StartDateIsBeforeAfter;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.common.multiac.IMultiAutoCompleteSource;
import cz.larpovadatabaze.components.common.multiac.MultiAutoCompleteComponent;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.ChooseLabelsPanel;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.MailClient;
import cz.larpovadatabaze.validator.AtLeastOneRequiredLabelValidator;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

abstract public class CreateEventPanel extends AbstractCsldPanel<Event> {
    private static final int AUTOCOMPLETE_CHOICES = 10;

    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private GameService gameService;
    @SpringBean
    private MailClient mailClient;

    private GLatLng lastSelectedLocation;
    private TextField<String> name;
    private NumberTextField amountOfPlayers;
    private TextField<String> web;
    private TextArea description;
    private ChooseLabelsPanel chooseLabels;

    private List<Label> labelsToTransfer;
    private GMap map;

    private String customLocation;

    public CreateEventPanel(String id, IModel<Event> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm<Event> createEvent = new ValidatableForm<>("addEvent", new CompoundPropertyModel<>(getModel()));
        createEvent.setOutputMarkupId(true);

        name = new RequiredTextField<>("name");
        name.setOutputMarkupId(true);
        name.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(name);
            }
        });
        createEvent.add(name);
        createEvent.add(new CsldFeedbackMessageLabel("nameFeedback", name, "form.event.nameHint"));

        Options czechCalendar = new Options();
        czechCalendar.set("start_weekday", 0);
        FormComponent<Date> from = new DatePicker("from", "dd.MM.yyyy", czechCalendar).setRequired(true);
        createEvent.add(from);
        createEvent.add(new CsldFeedbackMessageLabel("fromFeedback", from, "form.event.fromHint"));

        FormComponent<Date> to = new DatePicker("to", "dd.MM.yyyy", czechCalendar).setRequired(true);
        createEvent.add(to);
        createEvent.add(new CsldFeedbackMessageLabel("toFeedback", to, "form.event.toHint"));

        createEvent.add(new StartDateIsBeforeAfter(from, to));

        amountOfPlayers = new NumberTextField<>("amountOfPlayers");
        amountOfPlayers.setOutputMarkupId(true);
        amountOfPlayers.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(amountOfPlayers);
            }
        });
        amountOfPlayers.setRequired(true);
        createEvent.add(amountOfPlayers);
        createEvent.add(new CsldFeedbackMessageLabel("amountOfPlayersFeedback", amountOfPlayers, "form.event.amountOfPlayersHint"));

        // Web
        web = new TextField<>("web");
        web.setOutputMarkupId(true);
        web.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(web);
            }
        });
        createEvent.add(web);
        createEvent.add(new CsldFeedbackMessageLabel("webFeedback", web, "form.game.webHint"));

        RequiredTextField location = new RequiredTextField<String>("loc");
        createEvent.add(location);
        createEvent.add(new CsldFeedbackMessageLabel("locFeedback", location, "form.event.locationHint"));

        WebMarkupContainer gamesWrapper = new WebMarkupContainer("gamesWrapper");
        createEvent.add(gamesWrapper);

        // Choose from games to associate event with.
        addGamesInput(gamesWrapper);
        // Create new game to associate with this event.
        addCreateGameButton(gamesWrapper, createEvent);


        WebMarkupContainer descriptionWrapper = new WebMarkupContainer("descriptionWrapper");
        createEvent.add(descriptionWrapper);
        description = (TextArea) new TextArea<String>("description").setRequired(true);
        description.setOutputMarkupId(true);
        description.add(new CSLDTinyMceBehavior());
        descriptionWrapper.add(description);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("descriptionFeedback", description, descriptionWrapper, "form.event.descriptionHint"));

        chooseLabels = new ChooseLabelsPanel("labels", new IModel<List<Label>>() {
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
        chooseLabels.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                labelsToTransfer = chooseLabels.getConvertedInput();
                target.add(chooseLabels);
            }
        });
        createEvent.add(chooseLabels);
        WebMarkupContainer labelsFeedbackWrapper = new WebMarkupContainer("labelsFeedbackWrapper");
        createEvent.add(labelsFeedbackWrapper);
        labelsFeedbackWrapper.add(new CsldFeedbackMessageLabel("labelsFeedback", chooseLabels, labelsFeedbackWrapper, null));

        createEvent.add(new TextField<>("customLocation", new PropertyModel<String>(this, "customLocation")).add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {}
        }));
        createEvent.add(new Button("goToLocation").add(new AjaxFormComponentUpdatingBehavior("click") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Double latitude = null, longitude = null;
                if(customLocation.matches("^[0-9]+(.[0-9]+)?N, [0-9]+(.[0-9]+)?E$")) {
                    // Mapy.cz format
                    String location = customLocation.replace("N", "").replace("E", "");
                    latitude = Double.parseDouble(location.split(",")[0]);
                    longitude = Double.parseDouble(location.split(",")[1]);
                } else if(customLocation.matches("^^[0-9]+(.[0-9]+)?, [0-9]+(.[0-9]+)?$")) {
                    // Google maps format
                    latitude = Double.parseDouble(customLocation.split(",")[0]);
                    longitude = Double.parseDouble(customLocation.split(",")[1]);
                }

                if(latitude != null && longitude != null) {
                    map.setCenter(new GLatLng(latitude, longitude));
                }
            }
        }));

        addMap(createEvent, ((Event) getDefaultModelObject()).getLocation());

        add(createEvent);

        final AjaxButton submit = new AjaxButton("submit") {
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

                List<Game> previous = new ArrayList<>(event.getGames());
                if(createEvent.isValid()) {
                    event.setGames((List<Game>) ((MultiAutoCompleteComponent)createEvent.get("gamesWrapper:games")).getConvertedInput());
                }

                new DatabaseEvents(sessionFactory.getCurrentSession()).store(event);
                if(event != null && event.getGames() != null && event.getGames().size() > 0) {
                    for(Game game: event.getGames()) {
                        if(previous.contains(game)) {
                            continue;
                        }
                        for(UserPlayedGame interested: game.getPlayed()) {
                            if(interested.getStateEnum() == UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY) {
                                String url = CreateEventPanel.this.urlFor(DetailOfEventPage.class, DetailOfEventPage.pageParameters(event)).toString();
                                String content = "Byla přidána událost, která se váže ke hře, kterou máte nastavenou jako chci hrát. Odkaz: http://larpovadatabaze.cz/" + url;
                                mailClient.sendMail(content, interested.getPlayerOfGame().getPerson().getEmail(), "Do kalendáře byla přidána událost ke hře, která vás zajímá.");
                            }
                        }
                    }
                }

                onCsldAction(target, form);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getAjaxCallListeners().add(new AjaxCallListener()
                        .onBefore("$('#" + getMarkupId() + "').prop('disabled',true);")
                        .onComplete("$('#" + getMarkupId() + "').prop('disabled',false);"));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if(!createEvent.isValid()){
                    target.add(getParent());
                }
            }
        };

        createEvent.add(submit.add(new TinyMceAjaxSubmitModifier()));
    }

    private void addMap(Form container, Location location){
        map = new GMap("map", new GMapHeaderContributor("http", "AIzaSyC8K3jrJMl52-Mswi2BsS5UVKDZIT4GWh8")); // TODO: Restrict usage of the key.
        map.setStreetViewControlEnabled(false);
        map.setScaleControlEnabled(true);
        map.setScrollWheelZoomEnabled(true);
        map.setCenter(new GLatLng(49.9076134, 14.8630565)); // TODO: Move to the configuration.
        map.setZoom(7);

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
            map.setCenter(lastSelectedLocation);
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

    private void addCreateGameButton(WebMarkupContainer gamesWrapper, Form createEvent) {
        final ModalWindow createGameModal = new ModalWindow("createGame");
        createGameModal.setResizable(false);
        createGameModal.setInitialHeight(100);
        createGameModal.setInitialWidth(100);
        createGameModal.setWidthUnit("%");
        createGameModal.setHeightUnit("%");

        createGameModal.setTitle("Vytvořit hru");
        createGameModal.setCookieName("create-game");

        add(createGameModal);

        gamesWrapper.add(new AjaxButton("createGameBtn"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                handleModalOpen(createGameModal, target);
            }
        }).add(new TinyMceAjaxSubmitModifier()));
        createEvent.add(new AjaxButton("createGameSpecButton"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                handleModalOpen(createGameModal, target);
            }
        }).add(new TinyMceAjaxSubmitModifier()));
    }

    private void handleModalOpen(ModalWindow createGameModal, AjaxRequestTarget target) {
        Game game = Game.getEmptyGame();
        Event toUse = getModelObject();

        if(labelsToTransfer != null) {
            game.setLabels(labelsToTransfer);
        }
        game.setName(toUse.getName());
        game.setWeb(toUse.getWeb());
        game.setPlayers(toUse.getAmountOfPlayers());
        game.setDescription(toUse.getDescription());

        createGameModal.setContent(new CreateOrUpdateGamePanel(createGameModal.getContentId(), Model.of(game)){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);
                createGameModal.close(target);
            }
        });
        createGameModal.show(target);
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
