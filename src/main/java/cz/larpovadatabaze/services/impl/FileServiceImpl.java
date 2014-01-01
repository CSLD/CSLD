package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService.IImageResizingStrategy;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Time;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 20:44
 */
@Service
public class FileServiceImpl implements FileService, InitializingBean {

    private static final String PREVIEW_POSTFIX = "-p";
    private static final String DATA_DIR_ENV_KEY = "java:comp/env/csld/dataDir";

    /**
     * Base data directory
     */
    private String dataDir;

    /**
     * Callback to write a file
     */
    private static class FileWriteCallback extends AbstractResource.WriteCallback {

        private final File file;

        /**
         * @param file File to write
         */
        private FileWriteCallback(File file) {
            this.file = file;
        }

        @Override
        public void writeData(IResource.Attributes attributes) throws IOException {
            byte buf[] = new byte[65536];

            InputStream is = null;

            try {
                is = new FileInputStream(file);
                for(;;) {
                    int l = is.read(buf, 0, buf.length);
                    if (l <= 0) break; // EOF

                    OutputStream os = attributes.getResponse().getOutputStream();
                    os.write(buf, 0, l);
                }
            }
            finally {
                if (is != null) { try { is.close(); } catch(Exception e) {} }
            }
        }
    }

    /**
     * Resource implementing file resource
     */
    private static class FileResource extends AbstractResource {
        private final File file;
        private final String contentType;

        private FileResource(File file, String contentType) throws FileNotFoundException {
            if (!file.exists()) throw new FileNotFoundException();
            this.file = file;
            this.contentType = contentType;
        }

        @Override
        protected ResourceResponse newResourceResponse(Attributes attributes) {
            return respondWithFileStatic(file, contentType);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Lookup data dir in environment
        JndiTemplate jndi = new JndiTemplate();
        dataDir = (String)jndi.lookup(DATA_DIR_ENV_KEY);
    }

    private String getFileType(String fileName){
        String[] fileParts = fileName.trim().split("\\.");
        if(fileParts.length > 0){
            return fileParts[fileParts.length - 1];
        } else {
            return "";
        }
    }

    /**
     * It cleans space of the file given as parameter, if anything with the same name already existed.
     *
     * @param newFile
     */
    private void cleanFileIfExists(File newFile)
    {
        if (newFile.exists())
        {
            // Try to delete the file
            if (!Files.remove(newFile))
            {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    @Override
    public File getPathInDataDir(String relativeName) {
        return new File(dataDir, relativeName);
    }

    @Override
    public File getFilePreviewInDataDir(String relativeName) {
        String previewName;
        int di = relativeName.lastIndexOf('.');
        if (di > 0) {
            previewName = relativeName.substring(0, di)+PREVIEW_POSTFIX+relativeName.substring(di);
        }
        else {
            previewName = relativeName+PREVIEW_POSTFIX;
        }

        return getPathInDataDir(previewName);
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndPreviewAndReturnPath(FileUpload upload, IImageResizingStrategy fullImageResizingStrategy, IImageResizingStrategy previewResizingStrategy) {

        // Determine file type
        String fileType;
        String ct = upload.getContentType();
        if (StringUtils.isNotBlank(ct)) {
            int si = ct.lastIndexOf('/');
            if (si > 0) fileType = ct.substring(si+1);
            else fileType = ct;
        }
        else {
            fileType = getFileType(upload.getClientFileName());
        }

        // Generate name
        String fileName = RandomStringUtils.randomAlphanumeric(16) + "." + fileType;
        String dirName = fileName.substring(0,1)+"/"+fileName.substring(1,2);
        fileName = dirName+'/'+fileName;

        BufferedImage imageGameSized = null;

        // Create directories
        getPathInDataDir(dirName).mkdirs();

        // Create a new file
        try {

            File newFile = getPathInDataDir(fileName);
            BufferedImage sourceImage = ImageIO.read(upload.getInputStream());
            imageGameSized =  fullImageResizingStrategy.convertImage(sourceImage);

            // Check new file, delete if it already existed
            cleanFileIfExists(newFile);

            // Save to new file
            if(!newFile.createNewFile()){
                throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
            }
            ImageIO.write(imageGameSized, fileType, newFile);

            if (previewResizingStrategy != null) {
                // Create preview
                BufferedImage previewImage = previewResizingStrategy.convertImage(sourceImage);

                File previewFile = getFilePreviewInDataDir(fileName);
                if (!previewFile.createNewFile()) {
                    throw new RuntimeException("Unable to write file " + previewFile.getAbsolutePath());
                }
                ImageIO.write(previewImage, fileType, previewFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResizeAndSaveReturn(fileName, imageGameSized.getWidth(), imageGameSized.getHeight());
    }

    @Override
    public ResizeAndSaveReturn saveImageFileAndReturnPath(FileUpload upload, IImageResizingStrategy fullImageResizingStrategy) {
        return saveImageFileAndPreviewAndReturnPath(upload, fullImageResizingStrategy, null);
    }

    @Override
    public AbstractResource getFileResource(final String relativeName, final String contentType) throws FileNotFoundException{
        return new FileResource(getPathInDataDir(relativeName), contentType);
    }

    public static AbstractResource.ResourceResponse respondWithFileStatic(final File file, final String contentType) {
        AbstractResource.ResourceResponse res = new AbstractResource.ResourceResponse();

        if (!file.exists()) {
            res.setStatusCode(HttpStatus.NOT_FOUND.value());
            return res;
        }

        res.setLastModified(Time.millis(file.lastModified()));
        res.setContentType(contentType);
        res.setWriteCallback(new FileWriteCallback(file));

        return res;
    }

    @Override
    public AbstractResource.ResourceResponse respondWithFile(final File file, final String contentType) {
        return respondWithFileStatic(file, contentType);
    }

    @Override
    public AbstractResource.ResourceResponse respondWithFile(String relativeName, String contentType) {
        return respondWithFile(getPathInDataDir(relativeName), contentType);
    }

    @Override
    public void removeFiles(String relativePath) {
        if (relativePath != null) {
            File f = getPathInDataDir(relativePath);
            if (f.exists()) f.delete();

            f = getFilePreviewInDataDir(relativePath);
            if (f.exists()) f.delete();
        }
    }
}
