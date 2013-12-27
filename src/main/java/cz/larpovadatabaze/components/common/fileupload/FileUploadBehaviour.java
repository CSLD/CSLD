package cz.larpovadatabaze.components.common.fileupload;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.upload.FileItem;
import org.apache.wicket.util.upload.FileUploadException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

/**
 * User: Michal Kara
 * Date: 27.12.13
 * Time: 21:18
 */
public class FileUploadBehaviour extends AbstractAjaxBehavior {
    private final long maxUploadSize;
    private final IFileUploadCallback uploadCallback;
    private final String paramName;

    public FileUploadBehaviour(long maxUploadSize, IFileUploadCallback uploadCallback, String paramName) {
        this.maxUploadSize = maxUploadSize;
        this.uploadCallback = uploadCallback;
        this.paramName = paramName;
    }

    /**
     * Decides what should be the response's content type depending on the 'Accept' request header.
     * HTML5 browsers work with "application/json", older ones use IFrame to make the upload and the
     * response should be HTML.
     * Read http://blueimp.github.com/jQuery-File-Upload/ docs for more info.
     */
    protected boolean wantsHtml(String acceptHeader)
    {
        return !Strings.isEmpty(acceptHeader) && acceptHeader.contains("text/html");
    }


    /**
     * Prepares response to the file upload
     */
    protected void sendResponse(ServletWebRequest webRequest, WebResponse webResponse, List<FileItem> fileItems) throws FileUploadException, IOException {

        String accept = webRequest.getHeader("Accept");
        if (wantsHtml(accept))
        {
            // Internet Explorer
            RequestCycle.get().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("text/html", "utf-8", escapeHtml(generateJsonResponse(webRequest, webResponse, fileItems))));
        }
        else
        {
            // A real browser
            RequestCycle.get().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "utf-8", generateJsonResponse(webRequest, webResponse, fileItems)));
        }
    }

    protected String generateJsonResponse(ServletWebRequest webRequest, WebResponse webResponse, List<FileItem> files) {
        JSONArray json = new JSONArray();

        for (FileItem fileItem : files)
        {
            JSONObject fileJson = new JSONObject();

            try {
                fileJson.put("name", fileItem.getName());
                fileJson.put("size", fileItem.getSize());
                fileJson.put("delete_type", "POST");
            } catch (JSONException e) {
                try {
                    fileJson.put("error", e.getMessage());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            json.put(fileJson);
        }

        return json.toString();
    }

    @Override
    public void onRequest() {
        final ServletWebRequest webRequest = (ServletWebRequest) RequestCycle.get().getRequest();
        final WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();

        try
        {
            /* Get and process files */
            MultipartServletWebRequest multiPartRequest = webRequest.newMultipartWebRequest(Bytes.bytes(maxUploadSize), "ignored");

            Map<String, List<FileItem>> files = multiPartRequest.getFiles();
            List<FileItem> fileItems = files.get(paramName);

            uploadCallback.filesUploaded(fileItems);

            /* Send response */
            sendResponse(webRequest, webResponse, fileItems);
        }
        catch (Exception fux)
        {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fux.getMessage());
        }

    }
}
