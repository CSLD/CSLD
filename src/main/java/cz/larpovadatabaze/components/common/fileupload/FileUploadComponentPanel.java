package cz.larpovadatabaze.components.common.fileupload;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Michal Kara
 * Date: 24.12.13
 * Time: 21:44
 */
public class FileUploadComponentPanel extends Panel {
    private final static String PARAM_NAME = "files";

    private final long maxUploadSize;
    private final IFileUploadCallback uploadCallback;
    private final String acceptedTypes;

    private AbstractAjaxBehavior uploadDoneBehaviour;
    private AbstractAjaxBehavior uploadBehaviour;
    private WebMarkupContainer bar;

    private static final String INIT_JS = "new FileUploadUI('${componentMarkupId}', '${url}', '${paramName}', ${maxUploadSize}, ${acceptedTypes}, '${doneURL}');";

    /**
     * Create panel to upload files
     *
     * @param id Component id
     * @param maxUploadSize Maximum size of the uploads
     * @param acceptedTypes JavaScript regular expression (including the slashes and options) to match accepted file types
     * @param uploadCallback Callback called when files are uploaded
     */
    public FileUploadComponentPanel(String id, long maxUploadSize, String acceptedTypes, IFileUploadCallback uploadCallback) {
        super(id);

        this.maxUploadSize = maxUploadSize;
        this.uploadCallback = uploadCallback;
        this.acceptedTypes = acceptedTypes;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Add reference
        response.render(JavaScriptHeaderItem.forReference(FileUploadUIJavaScriptReference.get()));

        /* Build and add initialization Javascript */
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("componentMarkupId", bar.getMarkupId());
        variables.put("paramName", PARAM_NAME);
        variables.put("acceptedTypes", acceptedTypes);
        variables.put("maxUploadSize", maxUploadSize);
        variables.put("doneURL", uploadDoneBehaviour.getCallbackUrl());
        variables.put("url", uploadBehaviour.getCallbackUrl());

        response.render(OnDomReadyHeaderItem.forScript(new MapVariableInterpolator(INIT_JS, variables).toString()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // We need to output markup ID so that file upload can be initialized
        setOutputMarkupId(true);

        // Add bar
        bar = new WebMarkupContainer("bar");
        bar.setOutputMarkupId(true);
        add(bar);

        // Add behaviour to handle "upload done" callback
        uploadDoneBehaviour = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                // Propagate to our callback
                uploadCallback.fileUploadDone(target);
            }
        };
        add(uploadDoneBehaviour);

        uploadBehaviour = new FileUploadBehaviour(maxUploadSize, uploadCallback, PARAM_NAME);
        add(uploadBehaviour);
    }


}
