package cz.larpovadatabaze.components.common.fileupload;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.upload.FileItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Callback that is called when file is uploaded
 *
 * User: Michal Kara
 * Date: 24.12.13
 * Time: 22:05
 */
public interface IFileUploadCallback extends Serializable {

    /**
     * Called when files are uploaded while handling the uploaded request. You can process
     * data, but there is no AjaxRequestTarget
     *
     * @param fileItems Uploaded files
     */
    public void filesUploaded(List<FileItem> fileItems) throws IOException;

    /**
     * Called when file upload is done and action should be taken to
     * display the upload in UI
     *
     * @param target Ajax request target
     */
    public void fileUploadDone(AjaxRequestTarget target);
}
