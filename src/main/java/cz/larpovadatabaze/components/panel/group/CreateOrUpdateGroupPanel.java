package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.FileUtils;
import org.apache.wicket.Application;
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

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation of form used for creating groups. It may be used on more than one place. It can also be used for
 * editing group if group is given as parameter. If it isn't it gets empty one as a model.
 */
public abstract class CreateOrUpdateGroupPanel extends Panel {
    @SpringBean
    GroupService groupService;
    @SpringBean
    ImageService imageService;

    private FileUploadField fileUploadField;
    private List<FileUpload> images = new ArrayList<FileUpload>();

    public CreateOrUpdateGroupPanel(String id) {
        this(id, null);
    }

    public CreateOrUpdateGroupPanel(String id, CsldGroup group) {
        super(id);

        if(group == null) {
            group = CsldGroup.getEmptyGroup();
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
                ServletContext context = ((Csld) Application.get()).getServletContext();
                String realPath = context.getRealPath(Csld.getBaseContext());
                File baseFile = new File(realPath);

                // Create a new file
                File newFile = new File(baseFile, upload.getClientFileName());

                // Check new file, delete if it already existed
                FileUtils.cleanFileIfExists(newFile);
                try {
                    baseFile.mkdirs();
                    // Save to new file
                    if(!newFile.createNewFile()) {
                        throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
                    }
                    upload.writeTo(newFile);

                    Image image = new Image();
                    image.setPath(Csld.getBaseContext() + upload.getClientFileName());
                    imageService.insert(image);

                    group.setImage(image);
                    group.setImageId(image.getId());
                    if(groupService.insert(group)){
                        return true;
                    } else {
                        imageService.remove(image);
                        error("Skupinu se nepovedlo vložit");
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
