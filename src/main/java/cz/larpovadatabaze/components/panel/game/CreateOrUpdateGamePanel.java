package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.services.*;
import cz.larpovadatabaze.utils.FileUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.*;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This panel is used when you want to create or update game in the database.
 * It encapsulates relevant createOrUpdateGame with associated HTML markup.
 */
public abstract class CreateOrUpdateGamePanel extends Panel {
    @SpringBean
    GameService gameService;
    @SpringBean
    ImageService imageService;
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    GroupService groupService;
    @SpringBean
    VideoService videoService;

    private FileUploadField fileUploadField;
    private List<GenericModel<CsldUser>> authorsOfGame;
    private List<GenericModel<CsldGroup>> groupsOfGame;
    private ChooseLabelsPanel chooseLabels;
    @SuppressWarnings("unused")
    private List<FileUpload> images;

    public CreateOrUpdateGamePanel(String id, Game game) {
        super(id);

        if (game == null) {
            game = Game.getEmptyGame();
        }
        if(game.getVideo() == null) {
            game.setVideo(new Video());
            game.getVideo().setType(0);
        }

        final ValidatableForm<Game> createOrUpdateGame = new ValidatableForm<Game>("addGame", new CompoundPropertyModel<Game>(game));
        createOrUpdateGame.setOutputMarkupId(true);
        createOrUpdateGame.setMultiPart(true);
        createOrUpdateGame.setMaxSize(Bytes.kilobytes(1024));

        final org.apache.wicket.markup.html.image.Image plusIcon = new org.apache.wicket.markup.html.image.Image("plusIcon",
                new PackageResourceReference(Csld.class, Image.getPlusIconPath()));
        createOrUpdateGame.add(plusIcon);

        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("name").setRequired(true), createOrUpdateGame, "nameFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextArea<String>("description").setRequired(true), createOrUpdateGame, "descriptionFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("year"), createOrUpdateGame, "yearFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("web"), createOrUpdateGame, "webFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("hours"), createOrUpdateGame, "hoursFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("days"), createOrUpdateGame, "daysFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("players"), createOrUpdateGame, "playersFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("menRole"), createOrUpdateGame, "menRoleFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("womenRole"), createOrUpdateGame, "womenRoleFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<Integer>("bothRole"), createOrUpdateGame, "bothRoleFeedback"));
        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("video.path"), createOrUpdateGame, "videoPathFeedback"));

        fileUploadField = new FileUploadField("image", new PropertyModel<List<FileUpload>>(this,"images"));
        fileUploadField.setOutputMarkupId(true);
        createOrUpdateGame.add(addFeedbackPanel(fileUploadField, createOrUpdateGame, "imageFeedback"));

        addAuthorsInput(createOrUpdateGame, game);
        addGroupsInput(createOrUpdateGame, game);
        chooseLabels = new ChooseLabelsPanel("chooseLabels", game.getLabels());
        chooseLabels.setOutputMarkupId(true);
        createOrUpdateGame.add(chooseLabels);

        addCreateGroupButton(createOrUpdateGame);
        addCreateAuthorButton(createOrUpdateGame);
        addCreateLabelButton(createOrUpdateGame);

        createOrUpdateGame.add(new AjaxButton("submit"){


            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if(createOrUpdateGame.isValid()){
                    Game game = createOrUpdateGame.getModelObject();
                    if(saveOrUpdateGame(game)){
                        onCsldAction(target, form);
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
        });

        add(createOrUpdateGame);
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String nameOfFeedbackPanel){
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(addFeedbackTo);
        final FeedbackPanel feedbackPanel = new FeedbackPanel(nameOfFeedbackPanel, filter);
        feedbackPanel.setOutputMarkupId(true);
        addingFeedbackTo.add(feedbackPanel);
        addFeedbackTo.add(new AjaxFeedbackUpdatingBehavior("blur", feedbackPanel));
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

        RepeatableInputPanel<CsldGroup> groups = new RepeatableInputPanel<CsldGroup>("groups", groupIFactory,
                groupIValidator, game.getGroupAuthor(), groupService);
        createOrUpdateGame.add(groups);
        groupsOfGame = groups.getData();
    }

    private void addAuthorsInput(Form createOrUpdateGame, Game game){
        IFactory<CsldUser> userIFactory = new GenericFactory<CsldUser>(CsldUser.class);
        IValidator<CsldUser> userIValidator = new GenericValidator<CsldUser>(csldUserService);

        RepeatableInputPanel<CsldUser> authors = new RepeatableInputPanel<CsldUser>("authors", userIFactory,
                userIValidator, game.getAuthors(), csldUserService);
        createOrUpdateGame.add(authors);
        authorsOfGame = authors.getData();
    }

    private boolean saveOrUpdateGame(Game game) {
        game.setAdded(new Timestamp(new Date().getTime()));
        game.setLabels(chooseLabels.getSelected());

        List<CsldUser> authors = new ArrayList<CsldUser>();
        for (GenericModel<CsldUser> authorModel : authorsOfGame) {
            authors.add(authorModel.getObject());
        }
        game.setAuthors(authors);
        List<CsldGroup> groups = new ArrayList<CsldGroup>();
        for (GenericModel<CsldGroup> authorModel : groupsOfGame) {
            groups.add(authorModel.getObject());
        }
        game.setGroupAuthor(groups);

        if(game.getAmountOfComments() == null){
            game.setAmountOfComments(0);
        }
        if(game.getAmountOfRatings() == null){
            game.setAmountOfRatings(0);
        }
        if(game.getAmountOfPlayed() == null){
            game.setAmountOfPlayed(0);
        }
        if(game.getTotalRating() == null){
            game.setTotalRating(0d);
        }
        if(game.getImage() == null) {
            game.setImage(Image.getDefaultGame());
        }

        final List<FileUpload> uploads = fileUploadField.getFileUploads();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                String filePath = FileUtils.saveImageFileAndReturnPath(upload, game.getName(), 120, 120);
                try {
                    Image image = new Image();
                    image.setPath(filePath);
                    game.setImage(image);

                    if(game.getVideo() == null ||
                            game.getVideo().getPath() == null ||
                            game.getVideo().getPath().equals("") ||
                            game.getVideo().getPath().equals("Video")){
                        //TODO problem when internationalizating.
                        game.setVideo(null);
                    }

                    if(gameService.addGame(game)) {
                        return true;
                    } else {
                        error(getLocalizer().getString("game.cantAdd", this));
                        return false;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            if(game.getVideo() == null ||
                    game.getVideo().getPath() == null ||
                    game.getVideo().getPath().equals("") ||
                    game.getVideo().getPath().equals("Video")){
                game.setVideo(null);
            }

            if(gameService.addGame(game)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
