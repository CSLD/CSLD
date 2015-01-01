package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.GenericFactory;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.GenericValidator;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IFactory;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.RepeatableInputPanel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidator;

import java.util.ArrayList;
import java.util.Locale;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.behavior.ErrorClassAppender;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.JSPingBehavior;
import cz.larpovadatabaze.components.panel.ImagePanel;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.VideoService;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.utils.UserUtils;
import cz.larpovadatabaze.validator.AtLeastOneRequiredLabelValidator;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

/**
 * This panel is used when you want to create or update game in the database.
 * It encapsulates relevant createOrUpdateGame with associated HTML markup.
 */
public abstract class CreateOrUpdateGamePanel extends AbstractCsldPanel<Game> {
    @SpringBean
    GameService gameService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    GroupService groupService;
    @SpringBean
    VideoService videoService;
    LocaleProvider localeProvider = new CodeLocaleProvider();
    LanguageSolver sessionLanguageSolver = new SessionLanguageSolver();

    private ChooseLabelsPanel chooseLabels;
    private TextField<String> videoField;

    public CreateOrUpdateGamePanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = getModelObject();

        if(game.getVideo() == null) {
            game.setVideo(new Video());
            game.getVideo().setType(0);
        }

        final ValidatableForm<Game> createOrUpdateGame = new ValidatableForm<Game>("addGame", new CompoundPropertyModel<Game>(game));
        createOrUpdateGame.setOutputMarkupId(true);
        createOrUpdateGame.setMultiPart(true);
        createOrUpdateGame.setMaxSize(Bytes.kilobytes(1024));
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(createOrUpdateGame);
        createOrUpdateGame.add(new FeedbackPanel("feedback", filter).setOutputMarkupId(true));

        createOrUpdateGame.add(addFeedbackPanel(new RequiredTextField<String>("name").setLabel(Model.of("Jméno")), createOrUpdateGame, "nameFeedback"));
        TextArea description = (TextArea) new TextArea<String>("description").setRequired(true).setLabel(Model.of("Popis"));
        description.add(new CSLDTinyMceBehavior());
        createOrUpdateGame.add(addFeedbackPanel(description, createOrUpdateGame, "descriptionFeedback"));


        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("year").setLabel(Model.of("Rok")), createOrUpdateGame, "yearFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("web"), createOrUpdateGame, "webFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("galleryURL"), createOrUpdateGame, "galleryFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("photoAuthor"), createOrUpdateGame, "photoAuthorFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("hours").setLabel(Model.of("Hodiny")), createOrUpdateGame, "hoursFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("days").setLabel(Model.of("Dny")), createOrUpdateGame, "daysFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("players").setLabel(Model.of("Počet hráčů")), createOrUpdateGame, "playersFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("menRole").setLabel(Model.of("Počet mužů")), createOrUpdateGame, "menRoleFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("womenRole").setLabel(Model.of("Počet žen")), createOrUpdateGame, "womenRoleFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("bothRole").setLabel(Model.of("Počet obojetných")), createOrUpdateGame, "bothRoleFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(videoField = new TextField<String>("video.path"), createOrUpdateGame, "videoPathFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new ImagePanel("image"), createOrUpdateGame, "imageFeedback"));
        createOrUpdateGame.add(new CheckBox("ratingsDisabled") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("value", "on"); // Force value to "on", otherwise multipart form won't work
            }
        });
        createOrUpdateGame.add(new CheckBox("commentsDisabled") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("value", "on"); // Force value to "on", otherwise multipart form won't work
            }
        });

        final DropDownChoice<Locale> changeLocale =
                new DropDownChoice<Locale>("lang", new CodeLocaleProvider().availableLocale());
        createOrUpdateGame.add(addFeedbackPanel(changeLocale, createOrUpdateGame, "langFeedback"));

        addAuthorsInput(createOrUpdateGame, game);
        addGroupsInput(createOrUpdateGame, game);

        chooseLabels = new ChooseLabelsPanel("labels");
        chooseLabels.add(new AtLeastOneRequiredLabelValidator());
        createOrUpdateGame.add(addFeedbackPanel(chooseLabels, createOrUpdateGame, "labelsFeedback"));

        addCreateGroupButton(createOrUpdateGame);
        addCreateAuthorButton(createOrUpdateGame);
        addCreateLabelButton(createOrUpdateGame);

        createOrUpdateGame.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                // Process video
                Game game = (Game) form.getModelObject();
                String videoURL = videoService.getEmbedingURL(videoField.getConvertedInput());
                if (videoURL == null) {
                    // Bad URL - TODO - does not work - TODO
                    videoField.error("Nerozpoznané URL videa");
                }
                else {
                    // Set URL
                    Video v = game.getVideo();
                    if (v == null) {
                        v = new Video();
                        v.setType(0);
                        game.setVideo(v);
                    }
                    game.getVideo().setPath(videoURL);
                }

                //Process language
                Locale toBeSaved;
                if(game.getLang() != null) {
                    toBeSaved = localeProvider.transformToLocale(game.getLang());
                } else {
                    game.setAvailableLanguages(new ArrayList<GameHasLanguages>());
                    toBeSaved = sessionLanguageSolver.getLanguagesForUser().get(0);
                }

                if(game.getAvailableLanguages().isEmpty()) {
                    GameHasLanguages firstLanguage = new GameHasLanguages();
                    firstLanguage.setGame(game);
                    firstLanguage.setLanguageForGame(new Language(toBeSaved));
                    firstLanguage.setName(game.getName());
                    firstLanguage.setDescription(game.getDescription());
                    game.getAvailableLanguages().add(firstLanguage);
                }

                if(createOrUpdateGame.isValid()){
                    if(gameService.saveOrUpdate(game)){
                        onCsldAction(target, form);
                    } else {
                        error(getLocalizer().getString("game.cantAdd", this));
                        target.add(getParent());
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if(!createOrUpdateGame.isValid()){
                    target.add(getParent());
                }
            }
        }.add(new TinyMceAjaxSubmitModifier()));

        add(createOrUpdateGame);

        if (UserUtils.isSignedIn()) {
            add(new JSPingBehavior());
        }
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String nameOfFeedbackPanel){
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(addFeedbackTo);
        final FeedbackPanel feedbackPanel = new FeedbackPanel(nameOfFeedbackPanel, filter);
        feedbackPanel.setOutputMarkupId(true);
        addingFeedbackTo.add(feedbackPanel);
        addFeedbackTo.add(new AjaxFeedbackUpdatingBehavior("blur", feedbackPanel));
        addFeedbackTo.add(new ErrorClassAppender());
        return addFeedbackTo;
    }

    private void addCreateLabelButton(Form<Game> createOrUpdateGame) {
        final ModalWindow createlabelModal;
        add(createlabelModal = new ModalWindow("createLabel"));

        createlabelModal.setTitle("Vytvořit štítek");
        createlabelModal.setCookieName("create-label");

        createOrUpdateGame.add(new AjaxButton("createLabel"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                createlabelModal.setContent(new CreateOrUpdateLabelPanel(createlabelModal.getContentId()){
                    @Override
                    protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                        super.onCsldAction(target, form);
                        createlabelModal.close(target);
                        chooseLabels.reload(target);
                    }
                });
                createlabelModal.show(target);
            }
        }));
    }

    private void addCreateGroupButton(Form<Game> createOrUpdateGame) {
        final ModalWindow createGroupModal;
        add(createGroupModal = new ModalWindow("createGroup"));

        createGroupModal.setTitle("Vytvořit skupinu");
        createGroupModal.setCookieName("create-group");

        createOrUpdateGame.add(new AjaxButton("createGroup"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                createGroupModal.setContent(new CreateOrUpdateGroupPanel(createGroupModal.getContentId()){
                    @Override
                    protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                        super.onCsldAction(target, form);
                        createGroupModal.close(target);
                    }
                });
                createGroupModal.show(target);
            }
        }));
    }

    private void addCreateAuthorButton(Form<Game> createOrUpdateGame) {
        final ModalWindow createAuthorModal;
        add(createAuthorModal = new ModalWindow("createAuthor"));

        createAuthorModal.setTitle("Vytvořit autora");
        createAuthorModal.setCookieName("create-author");

        createOrUpdateGame.add(new AjaxButton("createAuthor"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                createAuthorModal.setContent(new CreateOrUpdateAuthorPanel(createAuthorModal.getContentId(), null){
                    @Override
                    protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                        super.onCsldAction(target, form);
                        createAuthorModal.close(target);
                    }
                });
                createAuthorModal.show(target);
            }
        }));
    }

    private void addGroupsInput(Form<Game> createOrUpdateGame, Game game) {
        IFactory<CsldGroup> groupIFactory = new GenericFactory<CsldGroup>(CsldGroup.class);
        IValidator<CsldGroup> groupIValidator = new GenericValidator<CsldGroup>(groupService);

        RepeatableInputPanel<CsldGroup> groups = new RepeatableInputPanel<CsldGroup>("groupAuthor", groupIFactory,
                groupIValidator, game.getGroupAuthor(), groupService);
        createOrUpdateGame.add(groups);
    }

    private void addAuthorsInput(Form createOrUpdateGame, Game game){
        IFactory<CsldUser> userIFactory = new GenericFactory<CsldUser>(CsldUser.class);
        IValidator<CsldUser> userIValidator = new GenericValidator<CsldUser>(csldUserService);

        RepeatableInputPanel<CsldUser> authors = new RepeatableInputPanel<CsldUser>("authors", userIFactory,
                userIValidator, game.getAuthors(), csldUserService);
        createOrUpdateGame.add(authors);
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
