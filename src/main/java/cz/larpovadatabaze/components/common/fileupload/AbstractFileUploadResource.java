package cz.larpovadatabaze.components.common.fileupload;

import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.AbstractResource;
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
 * Date: 24.12.13
 * Time: 22:00
 */
public abstract class AbstractFileUploadResource extends AbstractResource {

    private final long maxUploadSize;
    private final String paramName;
    private final IFileUploadCallback uploadCallback;

    /**
     * Create resource
     *
     * @param maxUploadSize Maximum upload size
     * @param paramName Name of parameter of the uploaded file
     * @param uploadCallback Callbacl called when files are uploaded
     */
    protected AbstractFileUploadResource(long maxUploadSize, String paramName, IFileUploadCallback uploadCallback) {
        this.maxUploadSize = maxUploadSize;
        this.paramName = paramName;
        this.uploadCallback = uploadCallback;
    }

    /**
     * Reads and stores the uploaded files
     */
    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        final ResourceResponse resourceResponse = new ResourceResponse();

        final ServletWebRequest webRequest = (ServletWebRequest) attributes.getRequest();

        try
        {
            MultipartServletWebRequest multiPartRequest = webRequest.newMultipartWebRequest(Bytes.bytes(maxUploadSize), "ignored");

            Map<String, List<FileItem>> files = multiPartRequest.getFiles();
            List<FileItem> fileItems = files.get(paramName);

            uploadCallback.filesUploaded(fileItems);

            prepareResponse(resourceResponse, webRequest, fileItems);
        }
        catch (Exception fux)
        {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, fux.getMessage());
        }

        return resourceResponse;
    }

    /**
     * Prepares response to the file upload
     */
    protected void prepareResponse(ResourceResponse resourceResponse, ServletWebRequest webRequest, List<FileItem> fileItems) throws FileUploadException, IOException {

        final String responseContent;
        String accept = webRequest.getHeader("Accept");
        if (wantsHtml(accept))
        {
            // Internet Explorer
            resourceResponse.setContentType("text/html");

            responseContent = escapeHtml(generateJsonResponse(resourceResponse, webRequest, fileItems));
        }
        else
        {
            // a real browser
            resourceResponse.setContentType("application/json");

            responseContent = generateJsonResponse(resourceResponse, webRequest, fileItems);
        }

        resourceResponse.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                attributes.getResponse().write(responseContent);
            }
        });
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
     * Should generate the response's body in JSON format
     *
     * @param resourceResponse
     * @param webRequest
     * @param files
     * @return
     */
    protected abstract String generateJsonResponse(ResourceResponse resourceResponse,
                                                   ServletWebRequest webRequest, List<FileItem> files);


}
