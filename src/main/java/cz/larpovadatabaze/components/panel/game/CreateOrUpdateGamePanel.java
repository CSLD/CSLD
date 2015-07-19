package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.components.common.JSPingBehavior;
import cz.larpovadatabaze.components.common.multiac.IMultiAutoCompleteSource;
import cz.larpovadatabaze.components.common.multiac.MultiAutoCompleteComponent;
import cz.larpovadatabaze.components.panel.UploadCoverImagePanel;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.Video;
import cz.larpovadatabaze.lang.LanguageSolver;
import cz.larpovadatabaze.lang.SessionLanguageSolver;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.VideoService;
import cz.larpovadatabaze.utils.UserUtils;
import cz.larpovadatabaze.validator.AtLeastOneRequiredLabelValidator;
import cz.larpovadatabaze.validator.NonEmptyAuthorsValidator;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import static cz.larpovadatabaze.lang.AvailableLanguages.availableLocaleNames;

/**
 * This panel is used when you want to create or update game in the database.
 * It encapsulates relevant createOrUpdateGame with associated HTML markup.
 */
public abstract class CreateOrUpdateGamePanel extends AbstractCsldPanel<Game> {
    private static final int AUTOCOMPLETE_CHOICES = 10;

    @SpringBean
    GameService gameService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    GroupService groupService;
    @SpringBean
    VideoService videoService;
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

        // Form
        final ValidatableForm<Game> createOrUpdateGame = new ValidatableForm<Game>("addGame", new CompoundPropertyModel<>(getModel()));
        createOrUpdateGame.setOutputMarkupId(true);
        createOrUpdateGame.setMultiPart(true);
        createOrUpdateGame.setMaxSize(Bytes.megabytes(5));

        // Messages
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(createOrUpdateGame);
        createOrUpdateGame.add(new FeedbackPanel("feedback", filter).setOutputMarkupId(true));

        // Name
        RequiredTextField name = new RequiredTextField<String>("name");
        createOrUpdateGame.add(name);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("nameFeedback", name, "form.game.nameHint"));


        // Labels
        chooseLabels = new ChooseLabelsPanel("labels", new IModel<List<Label>>() {
            @Override
            public List<Label> getObject() {
                return createOrUpdateGame.getModelObject().getLabels();
            }

            @Override
            public void setObject(List<Label> object) {
                createOrUpdateGame.getModelObject().setLabels(object);
            }

            @Override
            public void detach() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        chooseLabels.setOutputMarkupId(true);
        chooseLabels.add(new AtLeastOneRequiredLabelValidator());
        createOrUpdateGame.add(chooseLabels);
        WebMarkupContainer labelsFeedbackWrapper = new WebMarkupContainer("labelsFeedbackWrapper");
        createOrUpdateGame.add(labelsFeedbackWrapper);
        labelsFeedbackWrapper.add(new CsldFeedbackMessageLabel("labelsFeedback", chooseLabels, labelsFeedbackWrapper, null));

        addCreateLabelButton(createOrUpdateGame);

        // Description
        WebMarkupContainer descriptionWrapper = new WebMarkupContainer("descriptionWrapper");
        createOrUpdateGame.add(descriptionWrapper);
        TextArea description = (TextArea) new TextArea<String>("description").setRequired(true);
        description.add(new CSLDTinyMceBehavior());
        descriptionWrapper.add(description);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("descriptionFeedback", description, descriptionWrapper, "form.game.descriptionHint"));

        // Year
        TextField<Integer> year = new TextField<Integer>("year");
        createOrUpdateGame.add(year);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("yearFeedback", year, "form.game.yearHint"));

        // Language
        List<String> availableLanguages = new ArrayList<String>(availableLocaleNames());
        final DropDownChoice<String> lang = new DropDownChoice<String>("lang", availableLanguages);
        createOrUpdateGame.add(lang);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("langFeedback", lang, null));

        // Players
        TextField<Integer> players = new TextField<Integer>("players");
        createOrUpdateGame.add(players);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("playersFeedback", players, "form.game.playersHint"));

        // Women role
        TextField<Integer> womenRole = new TextField<Integer>("womenRole");
        createOrUpdateGame.add(womenRole);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("womenRoleFeedback", womenRole, "form.game.womenRoleHint"));

        // Men role
        TextField<Integer> menRole = new TextField<Integer>("menRole");
        createOrUpdateGame.add(menRole);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("menRoleFeedback", menRole, "form.game.menRoleHint"));

        // Both role
        TextField<Integer> bothRole = new TextField<Integer>("bothRole");
        createOrUpdateGame.add(bothRole);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("bothRoleFeedback", bothRole, "form.game.bothRoleHint"));

        // Web
        TextField<String> web = new TextField<String>("web");
        createOrUpdateGame.add(web);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("webFeedback", web, "form.game.webHint"));

        // Photo author
        TextField<String> photoAuthor = new TextField<String>("photoAuthor");
        createOrUpdateGame.add(photoAuthor);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("photoAuthorFeedback", photoAuthor, "form.game.photoAuthorHint"));

        // Gallery URL
        TextField<String> galleryURL = new TextField<String>("galleryURL");
        createOrUpdateGame.add(galleryURL);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("galleryURLFeedback", galleryURL, "form.game.galleryURLHint"));

        // Video path
        createOrUpdateGame.add(videoField = new TextField<String>("video", new IModel<String>() {
            @Override
            public String getObject() {
                return (getModelObject().getVideo()==null)?"":getModelObject().getVideo().getPath();
            }

            @Override
            public void setObject(String object) {
                // Will be processed on submit
            }

            @Override
            public void detach() {
            }
        }));
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("videoFeedback", videoField, "form.game.videoHint"));

        // Cover photo
        createOrUpdateGame.add(new UploadCoverImagePanel("coverImage"));

        // Ratings disabled
        createOrUpdateGame.add(new CheckBox("ratingsDisabled") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("value", "on"); // Force value to "on", otherwise multipart form won't work
            }
        });

        // Comments disabled
        createOrUpdateGame.add(new CheckBox("commentsDisabled") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("value", "on"); // Force value to "on", otherwise multipart form won't work
            }
        });


        WebMarkupContainer authorsWrapper = new WebMarkupContainer("authorsWrapper");
        createOrUpdateGame.add(authorsWrapper);

        addAuthorsInput(authorsWrapper);
        addGroupsInput(createOrUpdateGame, game);

        addCreateGroupButton(createOrUpdateGame);
        addCreateAuthorButton(authorsWrapper);


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
                String toBeSaved;
                if(game.getLang() != null) {
                    toBeSaved = game.getLang();
                } else {
                    game.setAvailableLanguages(new ArrayList<>());
                    toBeSaved = sessionLanguageSolver.getTextLangForUser().get(0);
                }

                if(game.getAvailableLanguages().isEmpty()) {
                    GameHasLanguages firstLanguage = new GameHasLanguages();
                    firstLanguage.setGame(game);
                    firstLanguage.setLanguage(toBeSaved);
                    firstLanguage.setName(game.getName());
                    firstLanguage.setDescription(game.getDescription());
                    game.getAvailableLanguages().add(firstLanguage);
                } else {
                    // Find existing locale
                    List<GameHasLanguages> actualLanguages = game.getAvailableLanguages();
                    for(GameHasLanguages language: actualLanguages) {
                        if(language.getLanguage().equals(toBeSaved)){
                            language.setName(game.getName());
                            language.setDescription(game.getDescription());
                        }
                    }
                }

                if(createOrUpdateGame.isValid()){
                    if(gameService.saveOrUpdate(game)){
                        onCsldAction(target, form);
                    } else {
                        error(getLocalizer().getString("game.cantAdd", form));
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

    private void addCreateLabelButton(Form<Game> createOrUpdateGame) {
        final ModalWindow createlabelModal;
        add(createlabelModal = new ModalWindow("createLabel"));

        createlabelModal.setTitle("Vytvořit štítek");
        createlabelModal.setCookieName("create-label");
        createlabelModal.setAutoSize(true);
        createlabelModal.setUseInitialHeight(false);
        createlabelModal.setInitialHeight(0);
        createlabelModal.setMinimalHeight(0);

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

    private void addCreateAuthorButton(WebMarkupContainer authorsWrapper) {
        final ModalWindow createAuthorModal;
        add(createAuthorModal = new ModalWindow("createAuthor"));

        createAuthorModal.setTitle("Vytvořit autora");
        createAuthorModal.setCookieName("create-author");

        authorsWrapper.add(new AjaxButton("createAuthorBtn"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
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

        MultiAutoCompleteComponent<CsldGroup> groups = new MultiAutoCompleteComponent<>("groupAuthor", new PropertyModel<>(getModelObject(), "groupAuthor"), new IMultiAutoCompleteSource<CsldGroup>() {
            @Override
            public Collection<CsldGroup> getChoices(String input) {
                return groupService.getFirstChoices(input, AUTOCOMPLETE_CHOICES);
            }

            @Override
            public CsldGroup getObjectById(Long id) {
                return groupService.getById(id.intValue());
            }
        });

        createOrUpdateGame.add(groups);
    }

    private void addAuthorsInput(WebMarkupContainer authorsWrapper){
        MultiAutoCompleteComponent<CsldUser> authors = new MultiAutoCompleteComponent<CsldUser>("authors", new PropertyModel<>(getModelObject(), "authors"), new IMultiAutoCompleteSource<CsldUser>() {
            @Override
            public Collection<CsldUser> getChoices(String input) {
                return csldUserService.getFirstChoices(input.toLowerCase(), AUTOCOMPLETE_CHOICES);
            }

            @Override
            public CsldUser getObjectById(Long id) {
                return csldUserService.getById(id.intValue());
            }
        });

        authorsWrapper.add(authors);

        authors.add(new NonEmptyAuthorsValidator());

        authorsWrapper.add(new CsldFeedbackMessageLabel("authorsFeedback", authors, authorsWrapper, null));
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
