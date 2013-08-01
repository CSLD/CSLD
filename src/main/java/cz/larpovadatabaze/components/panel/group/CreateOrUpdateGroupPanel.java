package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.FileUtils;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Button;
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
import java.util.List;

/**
 * Encapsulation of form used for creating groups. It may be used on more than one place. It can also be used for
 * editing group if group is given as parameter. If it isn't it gets empty one as a model.
 */
public class CreateOrUpdateGroupPanel extends Panel {
    @SpringBean
    GroupService groupService;
    @SpringBean
    ImageService imageService;

    private FileUploadField fileUploadField;

    public CreateOrUpdateGroupPanel(String id, CsldGroup group) {
        super(id);

        if(group == null) {
            group = CsldGroup.getEmptyGroup();
        }
        Form createGroup = new Form<CsldGroup>("addGroup", new CompoundPropertyModel<CsldGroup>(group)) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                validate();

                if (!hasError()) {
                    CsldGroup group = getModelObject();
                    saveGroupAndImage(group);
                }
            }
        };
        createGroup.setMultiPart(true);
        // Set maximum size to 1024K for demo purposes
        createGroup.setMaxSize(Bytes.kilobytes(1024));

        createGroup.add(new FeedbackPanel("feedback"));
        createGroup.add(new TextField<String>("name").setRequired(true));

        // Add one file input field
        createGroup.add(fileUploadField = new FileUploadField("image"));

        createGroup.add(new Button("submit"));

        add(createGroup);
    }

    private void saveGroupAndImage(CsldGroup group) {
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
                    if(!newFile.createNewFile()) {
                        throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
                    }
                    upload.writeTo(newFile);

                    Image image = new Image();
                    image.setPath(Csld.getBaseContext() + upload.getClientFileName());
                    imageService.insert(image);

                    group.setImage(image);
                    group.setImageId(image.getId());
                    groupService.insert(group);
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            groupService.insert(group);
        }
    }
}
