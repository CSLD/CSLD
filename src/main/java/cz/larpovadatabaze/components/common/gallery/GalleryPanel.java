package cz.larpovadatabaze.components.common.gallery;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates gallery of images
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 16:04
 */
public class GalleryPanel extends Panel {

    /**
     * Image preview width and height in picels
     */
    public static final int PREVIEW_WIDTH = 100;
    public static final int PREVIEW_HEIGHT = 100;

    private static final String GALLERY_JS_INIT = "new CSLDGallery('${componentId}', '${imageURL}', ${actionURL}, '${deleteConfirmationQuestion}', ${data});";

    /**
     * Image info bean
     */
    public static class ImageInfo {
        private final int id;
        private final String description;
        private final Boolean isTitle;
        private final int fullWidth;
        private final int fullHeight;

        public int getId() {
            return id;
        }

        /**
         * @param id Image ID - will be passed to callbacks
         * @param description Image description
         * @param isTitle Image is a title image. This is actually tristate: NULL = is not title and cannot be made title, TRUE = is title, FALSE = is not title
         */
        public ImageInfo(int id, String description, Boolean isTitle, int fullWidth, int fullHeight) {
            this.id = id;
            this.description = description;
            this.isTitle = isTitle;
            this.fullWidth = fullWidth;
            this.fullHeight = fullHeight;
        }
    }

    private final IGalleryDataProvider dataProvider;
    private final IGalleryManager manager;

    private WebMarkupContainer wrapper;
    private ResourceReference imageResourceReference;
    private AbstractAjaxBehavior actionsBehaviour;

    /**
     * @param id Panel id
     * @param model Model (list of image infos)
     * @param dataProvider Provider for image previews and image data
     * @param manager Gallery manager, specify NULL for read-only gallery
     */
    public GalleryPanel(String id, IModel<List<? extends ImageInfo>> model, IGalleryDataProvider dataProvider, IGalleryManager manager) {
        super(id, model);
        this.dataProvider = dataProvider;
        this.manager = manager;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Create wrapper
        wrapper = new WebMarkupContainer("gallery");
        wrapper.setOutputMarkupId(true);
        add(wrapper);

        // Create image resource & its reference
        final AbstractResource imageResource = new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ServletWebRequest request = (ServletWebRequest)attributes.getRequest();

                int id = attributes.getParameters().get("id").toInt();
                if (attributes.getParameters().get("full").isNull()) {
                    // Return preview
                    return dataProvider.getImagePreview(id);
                }
                else {
                    // Return full id
                    return dataProvider.getFullImage(id);
                }
            }
        };
        imageResourceReference = new ResourceReference(GalleryPanel.class, "image-resource") {
            @Override
            public IResource getResource() {
                return imageResource;
            }
        };

        if (manager != null) {
            // Create behaviour that handles actions
            actionsBehaviour = new AbstractAjaxBehavior() {
                @Override
                public void onRequest() {
                    // Get Ajax request target
                    WebApplication app = (WebApplication)getComponent().getApplication();
                    AjaxRequestTarget target = app.newAjaxRequestTarget(getComponent().getPage());

                    RequestCycle requestCycle = RequestCycle.get();
                    requestCycle.scheduleRequestHandlerAfterCurrent(target);

                    // Process parameters
                    IRequestParameters params = requestCycle.getRequest().getRequestParameters();
                    String action = params.getParameterValue("action").toString();

                    if ("update".equals(action)) {
                        int id = params.getParameterValue("imageId").toInt();
                        manager.setImageDescription(id, params.getParameterValue("newDescription").toString());
                    }
                    else if("publish".equals(action)) {
                        int id = params.getParameterValue("imageId").toInt();
                        manager.publishPhoto(id);
                    }
                    else if("hide".equals(action)) {
                        int id = params.getParameterValue("imageId").toInt();
                        manager.hidePhotoFromFront(id);
                    }
                    else if ("delete".equals(action)) {
                        int id = params.getParameterValue("imageId").toInt();
                        manager.deleteImage(id);
                    }
                    else if ("setOrder".equals(action)) {
                        List<Integer> ids = new ArrayList<Integer>();
                        for(String id : params.getParameterValue("imageIds").toString().split(",")) {
                            ids.add(Integer.valueOf(id));
                        }
                        manager.setImageOrder(ids);

                        // Redraw gallery & newly init the script
                        target.add(wrapper);
                        GalleryPanel.this.renderHead(target.getHeaderResponse());
                    }
                    else {
                        throw new IllegalArgumentException("Unknown action '"+action+"'");
                    }
                }
            };
            add(actionsBehaviour);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Render Javascript reference
        response.render(JavaScriptReferenceHeaderItem.forReference(GalleryJavaScriptReference.get()));

        /* Prepare javascript to initialize gallery */

        // Prepare image data
        JSONArray json = new JSONArray();
        try {
            for(ImageInfo ii : (List<ImageInfo>)getDefaultModelObject()) {
                JSONObject img = new JSONObject();
                img.put("id", ii.id);
                img.put("desc", ii.description);
                img.put("width", ii.fullWidth);
                img.put("height", ii.fullHeight);
                if (ii.isTitle != null) {
                    img.put("isTitle", ii.isTitle);
                }
                json.put(img);
            }
        }
        catch(JSONException e) {
            // Should not happen - rethrow as runtime
            throw new RuntimeException(e);
        }

        // Prepare parameters
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("componentId", wrapper.getMarkupId());
        variables.put("imageURL", urlFor(imageResourceReference, null));
        variables.put("actionURL", (actionsBehaviour != null)?("'"+actionsBehaviour.getCallbackUrl()+"'"):"null");
        variables.put("deleteConfirmationQuestion", "Opravdu smazat obrázek?");
        variables.put("data", json.toString());

        // Render JS
        response.render(OnDomReadyHeaderItem.forScript(new MapVariableInterpolator(GALLERY_JS_INIT, variables).toString()));
    }
}
