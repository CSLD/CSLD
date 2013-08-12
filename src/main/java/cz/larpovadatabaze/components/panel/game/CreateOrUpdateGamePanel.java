package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.FileUtils;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.*;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidator;

import javax.persistence.criteria.From;
import javax.servlet.ServletContext;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This panel is used when you want to create or update game in the database. It encapsulates relevant createOrUpdateGame with
 * associated HTML markup.
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

    private FileUploadField fileUploadField;
    private List<GenericModel<CsldUser>> authorsOfGame;
    private List<GenericModel<CsldGroup>> groupsOfGame;
    private String videoPath; // Used by model.
    private ChooseLabelsPanel chooseLabels;

    public CreateOrUpdateGamePanel(String id, Game game) {
        super(id);

        if (game == null) {
            game = Game.getEmptyGame();
        }
        final ValidatableForm<Game> createOrUpdateGame = new ValidatableForm<Game>("addGame", new CompoundPropertyModel<Game>(game));
        createOrUpdateGame.setOutputMarkupId(true);
        createOrUpdateGame.setMultiPart(true);
        createOrUpdateGame.setMaxSize(Bytes.kilobytes(1024));

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
        createOrUpdateGame.add(addFeedbackPanel(new TextField<String>("videoPath", new PropertyModel<String>(this, "videoPath")), createOrUpdateGame, "videoPathFeedback"));

        fileUploadField = new FileUploadField("image");
        createOrUpdateGame.add(addFeedbackPanel(fileUploadField, createOrUpdateGame, "imageFeedback"));

        addAuthorsInput(createOrUpdateGame, game);
        addGroupsInput(createOrUpdateGame, game);
        chooseLabels = new ChooseLabelsPanel("chooseLabels");
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
                    saveOrUpdateGame(game);
                    onCsldAction(target, form);
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

        createlabelModal.setContent(new CreateOrUpdateLabelPanel(createlabelModal.getContentId()){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);
                createlabelModal.close(target);
            }
        });
        createlabelModal.setTitle("Vytvořit štítek.");
        createlabelModal.setCookieName("create-label");

        createOrUpdateGame.add(new AjaxButton("createLabel"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                createlabelModal.show(target);
            }
        }));
    }

    private void addCreateGroupButton(Form<Game> createOrUpdateGame) {
        final ModalWindow createGroupModal;
        add(createGroupModal = new ModalWindow("createGroup"));

        createGroupModal.setContent(new CreateOrUpdateGroupPanel(createGroupModal.getContentId()){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);
                createGroupModal.close(target);
            }
        });
        createGroupModal.setTitle("Vytvořit skupinu.");
        createGroupModal.setCookieName("create-group");

        createOrUpdateGame.add(new AjaxButton("createGroup"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                createGroupModal.show(target);
            }
        }));
    }

    private void addCreateAuthorButton(Form<Game> createOrUpdateGame) {
        final ModalWindow createAuthorModal;
        add(createAuthorModal = new ModalWindow("createAuthor"));

        CreateOrUpdateAuthorPanel createAuthor = new CreateOrUpdateAuthorPanel(createAuthorModal.getContentId(), null){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);
                createAuthorModal.close(target);
            }
        };
        createAuthorModal.setContent(createAuthor);
        createAuthorModal.setTitle("Vytvořit autora.");
        createAuthorModal.setCookieName("create-author");

        createOrUpdateGame.add(new AjaxButton("createAuthor"){}.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
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

    private void saveOrUpdateGame(Game game) {
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

        final List<FileUpload> uploads = fileUploadField.getFileUploads();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                ServletContext context = ((Csld) Application.get()).getServletContext();
                String realPath = context.getRealPath(Csld.getBaseContext());
                File baseFile = new File(realPath);

                // Create a new file
                File newFile = new File(baseFile, upload.getClientFileName());

                // Check new file, delete if it already existed
                FileUtils.cleanFileIfExists(newFile);
                try {
                    // Save to new file
                    if(!newFile.mkdirs() || !newFile.createNewFile()){
                        throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
                    }
                    upload.writeTo(newFile);

                    Image image = new Image();
                    image.setPath(realPath);
                    imageService.insert(image);

                    game.setImage(image);
                    gameService.addGame(game);
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            gameService.addGame(game);
        }
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
