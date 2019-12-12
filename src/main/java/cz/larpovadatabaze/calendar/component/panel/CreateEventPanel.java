package cz.larpovadatabaze.calendar.component.panel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.datepicker.DatePicker;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.calendar.Location;
import cz.larpovadatabaze.calendar.component.common.TimeTextField;
import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.component.validator.StartDateIsBeforeAfter;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.common.multiac.IMultiAutoCompleteSource;
import cz.larpovadatabaze.components.common.multiac.MultiAutoCompleteComponent;
import cz.larpovadatabaze.components.panel.game.ChooseLabelsPanel;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.MailClient;
import cz.larpovadatabaze.validator.AtLeastOneRequiredLabelValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.GMapHeaderContributor;
import org.wicketstuff.gmap.api.GLatLng;
import org.wicketstuff.gmap.api.GMarker;
import org.wicketstuff.gmap.api.GMarkerOptions;
import org.wicketstuff.gmap.event.ClickListener;

import java.util.*;

public abstract class CreateEventPanel extends AbstractCsldPanel<Event> {
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
    private ChooseLabelsPanel chooseLabels;

    private List<Label> labelsToTransfer;
    private GMap map;

    // Used for holding specific information from non model fields.
    private String customLocation;
    private String fromTime;
    private String toTime;

    public CreateEventPanel(String id, IModel<Event> model) {
        super(id, model);

        Event event = model.getObject();
        if(event != null) {
            if(event.getFrom() != null) {
                fromTime = String.valueOf(event.getFrom().get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(event.getFrom().get(Calendar.MINUTE));
            }

            if(event.getTo() != null) {
                toTime = String.valueOf(event.getTo().get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(event.getTo().get(Calendar.MINUTE));
            }
        }
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

        // OnUpdate set to to the same date.
        Options czechCalendar = new Options();
        czechCalendar.set("start_weekday", 0);
        FormComponent<Date> from = new DatePicker("from", "dd.MM.yyyy", czechCalendar).setRequired(true);
        createEvent.add(from);
        createEvent.add(new CsldFeedbackMessageLabel("fromFeedback", from, "form.event.fromHint"));

        final FormComponent<String> fromTimeField = new TimeTextField("fromTime", new PropertyModel(this, "fromTime"));
        fromTimeField.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {}
        });
        createEvent.add(fromTimeField);
        createEvent.add(new CsldFeedbackMessageLabel("fromTimeFeedback", fromTimeField, "form.event.fromTimeHint"));

        FormComponent<Date> to = new DatePicker("to", "dd.MM.yyyy", czechCalendar).setRequired(true);
        createEvent.add(to);
        createEvent.add(new CsldFeedbackMessageLabel("toFeedback", to, "form.event.toHint"));

        FormComponent<String> toTimeField = new TimeTextField("toTime", new PropertyModel(this, "toTime"));
        toTimeField.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {}
        });
        createEvent.add(toTimeField);
        createEvent.add(new CsldFeedbackMessageLabel("toTimeFeedback", toTimeField, "form.event.toTimeHint"));

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
        DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbar");
        final WysiwygEditor editor = new WysiwygEditor("description", toolbar);

        final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        descriptionWrapper.add(feedback);
        descriptionWrapper.add(toolbar, editor);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("descriptionFeedback", editor, descriptionWrapper, "form.event.descriptionHint"));

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

        final AjaxButton submit = new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                Transaction storeEventInTransaction = sessionFactory.getCurrentSession().beginTransaction();

                Event event = CreateEventPanel.this.getModel().getObject();
                if(lastSelectedLocation != null) {
                    event.setLocation(new Location(lastSelectedLocation.getLat(), lastSelectedLocation.getLng()));
                }
                if(event.getAddedBy() == null) {
                    event.setAddedBy(CsldAuthenticatedWebSession.get().getLoggedUser());
                }

                List<Game> previous;
                if(event.getGames() != null) {
                    previous = new ArrayList<>(event.getGames());
                } else {
                    previous = new ArrayList<>();
                }
                if(createEvent.isValid()) {
                    event.setGames((List<Game>) ((MultiAutoCompleteComponent)createEvent.get("gamesWrapper:games")).getConvertedInput());
                }

                if(StringUtils.isNotBlank(fromTime)){
                    if(fromTime.split(":").length == 2) {
                        Calendar from = event.getFrom();
                        from.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromTime.split(":")[0]));
                        from.set(Calendar.MINUTE, Integer.parseInt(fromTime.split(":")[1]));
                        event.setFrom(from.getTime());
                    }
                }

                if(StringUtils.isNotBlank(toTime)){
                    if(toTime.split(":").length == 2) {
                        Calendar to = event.getTo();
                        to.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTime.split(":")[0]));
                        to.set(Calendar.MINUTE, Integer.parseInt(toTime.split(":")[1]));
                        event.setTo(to.getTime());
                    }
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

                storeEventInTransaction.commit();
                onCsldAction(target, event);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getAjaxCallListeners().add(new AjaxCallListener()
                        .onBefore("$('#" + getMarkupId() + "').prop('disabled',true);")
                        .onComplete("$('#" + getMarkupId() + "').prop('disabled',false);"));
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);
                if(!createEvent.isValid()){
                    target.add(getParent());
                }
            }
        };
        createEvent.add(submit);
        add(createEvent);
    }

    private void addMap(Form container, Location location){
        map = new GMap("map", new GMapHeaderContributor("https", "AIzaSyC8K3jrJMl52-Mswi2BsS5UVKDZIT4GWh8")); // TODO: Restrict usage of the key.
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
        }));
        createEvent.add(new AjaxButton("createGameSpecButton"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                handleModalOpen(createGameModal, target);
            }
        }));
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
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);
                createGameModal.close(target);
            }
        });
        createGameModal.show(target);
    }

    protected void onCsldAction(AjaxRequestTarget target, Object object){}
}
