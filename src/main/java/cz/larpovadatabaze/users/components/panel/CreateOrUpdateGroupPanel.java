package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.AbstractCsldPanel;
import cz.larpovadatabaze.common.components.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.common.components.ValidatableForm;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.Image;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.validator.UniqueGroupValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
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
public abstract class CreateOrUpdateGroupPanel extends AbstractCsldPanel<CsldGroup> {
    private static final int GROUP_ICON_SIZE=120;

    @SpringBean
    CsldGroups csldGroups;
    @SpringBean
    Images sqlImages;
    @SpringBean
    UniqueGroupValidator uniqueGroupValidator;
    @SpringBean
    FileService fileService;
    @SpringBean
    ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;

    private FileUploadField fileUploadField;
    @SuppressWarnings("unused")
    private List<FileUpload> images = new ArrayList<>();

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

        setDefaultModel(new CompoundPropertyModel<>(group));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final ValidatableForm<CsldGroup> createGroup = new ValidatableForm<>("addGroup", getModel());
        createGroup.setMultiPart(true);
        // Set maximum size to 1024K for demo purposes
        createGroup.setMaxSize(Bytes.kilobytes(1024));
        createGroup.setOutputMarkupId(true);

        TextField<String> name = new TextField<>("name");
        name.setRequired(true);
        name.add(uniqueGroupValidator);
        createGroup.add(name);
        createGroup.add(new CsldFeedbackMessageLabel("nameFeedback", name, null));

        // Add one file input field

        fileUploadField = new FileUploadField("image", new PropertyModel<>(this, "images"));
        createGroup.add(fileUploadField);

        createGroup.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);

                if(createGroup.isValid()){
                    CsldGroup group = createGroup.getModelObject();
                    if(saveGroupAndImage(group)){
                        onCsldAction(target, group);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);

                target.add(CreateOrUpdateGroupPanel.this);
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
                    sqlImages.saveOrUpdate(image);

                    group.setImage(image);
                    if (csldGroups.saveOrUpdate(group)) {
                        return true;
                    } else {
                        images.remove(image);
                        error(getLocalizer().getString("group.cantAdd", this));
                        return false;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            return csldGroups.saveOrUpdate(group);
        }
        return false;
    }

    protected void onCsldAction(AjaxRequestTarget target, Object object){}
}
