package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.validator.UniqueGroupValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation of form used for creating groups. It may be used on more than one place. It can also be used for
 * editing group if group is given as parameter. If it isn't it gets empty one as a model.
 */
public abstract class CreateOrUpdateGroupPanel extends Panel {
    private static final int GROUP_ICON_SIZE=120;

    @SpringBean
    GroupService groupService;
    @SpringBean
    ImageService imageService;
    @SpringBean
    UniqueGroupValidator uniqueGroupValidator;
    @SpringBean
    FileService fileService;
    @SpringBean
    ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;

    private FileUploadField fileUploadField;
    @SuppressWarnings("unused")
    private List<FileUpload> images = new ArrayList<FileUpload>();

    public CreateOrUpdateGroupPanel(String id) {
        this(id, null);
    }

    public CreateOrUpdateGroupPanel(String id, CsldGroup group) {
        super(id);

        if(group == null) {
            group = CsldGroup.getEmptyGroup();
        } else {
            uniqueGroupValidator.setUpdateExisting(true);
        }
        final ValidatableForm<CsldGroup> createGroup = new ValidatableForm<CsldGroup>("addGroup", new CompoundPropertyModel<CsldGroup>(group)) {};
        createGroup.setMultiPart(true);
        // Set maximum size to 1024K for demo purposes
        createGroup.setMaxSize(Bytes.kilobytes(1024));
        createGroup.setOutputMarkupId(true);

        TextField<String> name = new TextField<String>("name");
        name.setRequired(true);
        ComponentFeedbackMessageFilter nameFilter = new ComponentFeedbackMessageFilter(name);
        final FeedbackPanel nameFeedback = new FeedbackPanel("nameFeedback", nameFilter);
        nameFeedback.setOutputMarkupId(true);
        createGroup.add(nameFeedback);
        name.add(new AjaxFeedbackUpdatingBehavior("blur", nameFeedback));
        name.add(uniqueGroupValidator);
        createGroup.add(name);

        // Add one file input field

        fileUploadField = new FileUploadField("image", new PropertyModel<List<FileUpload>>(this, "images"));
        ComponentFeedbackMessageFilter imageFilter = new ComponentFeedbackMessageFilter(fileUploadField);
        final FeedbackPanel imageFeedback = new FeedbackPanel("imageFeedback", imageFilter);
        imageFeedback.setOutputMarkupId(true);
        createGroup.add(imageFeedback);
        name.add(new AjaxFeedbackUpdatingBehavior("blur", imageFeedback));
        createGroup.add(fileUploadField);

        createGroup.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if(createGroup.isValid()){
                    CsldGroup group = createGroup.getModelObject();
                    if(saveGroupAndImage(group)){
                        onCsldAction(target, form);
                    }
                }
            }
        });
        add(createGroup);
    }

    private boolean saveGroupAndImage(CsldGroup group) {
        final List<FileUpload> uploads = fileUploadField.getFileUploads();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                String filePath = fileService.saveImageFileAndReturnPath(upload, imageResizingStrategyFactoryService.getCuttingSquareStrategy(GROUP_ICON_SIZE, 50)).path;
                try {
                    Image image = new Image();
                    image.setPath(filePath);
                    imageService.insert(image);

                    group.setImage(image);
                    if(groupService.insert(group)){
                        return true;
                    } else {
                        imageService.remove(image);
                        error(getLocalizer().getString("group.cantAdd", this));
                        return false;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            return groupService.insert(group);
        }
        return false;
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
