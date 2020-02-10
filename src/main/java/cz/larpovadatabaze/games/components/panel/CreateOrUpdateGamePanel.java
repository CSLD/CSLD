package cz.larpovadatabaze.games.components.panel;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.common.components.JSPingBehavior;
import cz.larpovadatabaze.common.components.ValidatableForm;
import cz.larpovadatabaze.common.components.multiac.IMultiAutoCompleteSource;
import cz.larpovadatabaze.common.components.multiac.MultiAutoCompleteComponent;
import cz.larpovadatabaze.common.entities.*;
import cz.larpovadatabaze.common.models.UploadedFile;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Videos;
import cz.larpovadatabaze.games.validator.AtLeastOneRequiredLabelValidator;
import cz.larpovadatabaze.users.components.panel.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.users.components.panel.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.validator.NonEmptyAuthorsValidator;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import java.util.Collection;
import java.util.List;

/**
 * This panel is used when you want to create or update game in the database.
 * It encapsulates relevant createOrUpdateGame with associated HTML markup.
 */
public abstract class CreateOrUpdateGamePanel extends AbstractCsldPanel<Game> {
    private static final int AUTOCOMPLETE_CHOICES = 10;

    @SpringBean
    Games games;
    @SpringBean
    CsldUsers csldUsers;
    @SpringBean
    CsldGroups csldGroups;
    @SpringBean
    Videos videos;
    @SpringBean
    AppUsers appUsers;
    @SpringBean
    FileService files;

    private ChooseLabelsPanel chooseLabels;
    private TextField<String> videoField;
    private UploadCoverImagePanel coverImagePanel;
    private FileUploadField blueprint;

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
        chooseLabels = new ChooseLabelsPanel("labels", new IModel<>() {
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
        DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbar");
        final WysiwygEditor editor = new WysiwygEditor("description", toolbar);

        final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        descriptionWrapper.add(feedback);
        descriptionWrapper.add(toolbar, editor);
        descriptionWrapper.add(new CsldFeedbackMessageLabel("descriptionFeedback", editor, descriptionWrapper, "form.game.descriptionHint"));

        // Year
        TextField<Integer> year = new TextField<Integer>("year");
        createOrUpdateGame.add(year);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("yearFeedback", year, "form.game.yearHint"));

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

        // Hours
        TextField<Integer> hours = new TextField<Integer>("hours");
        createOrUpdateGame.add(hours);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("hoursFeedback", bothRole, "form.game.hoursHint"));

        // Days
        TextField<Integer> days = new TextField<Integer>("days");
        createOrUpdateGame.add(days);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("daysFeedback", bothRole, "form.game.daysHint"));

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
        createOrUpdateGame.add(videoField = new TextField<>("video", new IModel<>() {
            @Override
            public String getObject() {
                return (getModelObject().getVideo() == null) ? "" : getModelObject().getVideo().getPath();
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

        coverImagePanel = new UploadCoverImagePanel("coverImage");
        // Cover photo
        createOrUpdateGame.add(coverImagePanel);

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

        blueprint = new FileUploadField("gameBlueprint", new IModel<>() {
            private List<FileUpload> uploads;

            @Override
            public List<FileUpload> getObject() {
                return uploads;
            }

            @Override
            public void setObject(List<FileUpload> uploads) {
                this.uploads = uploads;
            }
        });
        createOrUpdateGame.add(blueprint);
        createOrUpdateGame.add(new CsldFeedbackMessageLabel("blueprintFeedback", bothRole, "form.game.blueprintHint"));

        WebMarkupContainer authorsWrapper = new WebMarkupContainer("authorsWrapper");
        createOrUpdateGame.add(authorsWrapper);

        addAuthorsInput(authorsWrapper);
        addGroupsInput(createOrUpdateGame, game);

        addCreateGroupButton(createOrUpdateGame);
        addCreateAuthorButton(authorsWrapper);


        createOrUpdateGame.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                coverImagePanel.convertInput();

                Game game = CreateOrUpdateGamePanel.this.getModelObject();
                game.setCoverImage(coverImagePanel.getConvertedInput());
                // TODO: Clean while moving the Game to the DTO model
                FileUpload blueprintFile = blueprint.getFileUpload();
                if (blueprintFile != null) {
                    String bluePrintPath = files.saveFileAndReturnPath(new UploadedFile(blueprintFile));
                    game.setBlueprintPath(bluePrintPath);
                }

                if (createOrUpdateGame.isValid()) {
                    String videoUrl = videoField.getConvertedInput();
                    // TODO: find why when editing form the converted input isn't propagated.
                    game.setVideo(new Video(videos.getEmbedingURL(videoUrl)));
                    game.setGroupAuthor((List<CsldGroup>) ((MultiAutoCompleteComponent) createOrUpdateGame.get("groupAuthor")).getConvertedInput());
                    game.setAuthors((List<CsldUser>) ((MultiAutoCompleteComponent) createOrUpdateGame.get("authorsWrapper:authors")).getConvertedInput());
                    if (games.saveOrUpdate(game)) {
                        onCsldAction(target, game);
                    } else {
                        error(getLocalizer().getString("game.cantAdd", CreateOrUpdateGamePanel.this));
                        target.add(getParent());
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);
                if(!createOrUpdateGame.isValid()){
                    target.add(getParent());
                }
            }
        });

        add(createOrUpdateGame);

        if (appUsers.isSignedIn()) {
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
                    protected void onCsldAction(AjaxRequestTarget target, Object object) {
                        super.onCsldAction(target, object);
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
                    protected void onCsldAction(AjaxRequestTarget target, Object object) {
                        super.onCsldAction(target, object);
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
                    protected void onCsldAction(AjaxRequestTarget target, Object object) {
                        super.onCsldAction(target, object);
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
                return csldGroups.getFirstChoices(input, AUTOCOMPLETE_CHOICES);
            }

            @Override
            public CsldGroup getObjectById(Long id) {
                return csldGroups.getById(id.intValue());
            }
        });

        createOrUpdateGame.add(groups);
    }

    private void addAuthorsInput(WebMarkupContainer authorsWrapper){
        MultiAutoCompleteComponent<CsldUser> authors = new MultiAutoCompleteComponent<CsldUser>("authors", new PropertyModel<>(getModelObject(), "authors"), new IMultiAutoCompleteSource<CsldUser>() {
            @Override
            public Collection<CsldUser> getChoices(String input) {
                return csldUsers.getFirstChoices(input.toLowerCase(), AUTOCOMPLETE_CHOICES);
            }

            @Override
            public CsldUser getObjectById(Long id) {
                return csldUsers.getById(id.intValue());
            }
        });

        authorsWrapper.add(authors);

        authors.add(new NonEmptyAuthorsValidator());

        authorsWrapper.add(new CsldFeedbackMessageLabel("authorsFeedback", authors, authorsWrapper, null));
    }

    protected void onCsldAction(AjaxRequestTarget target, Object object){}
}
