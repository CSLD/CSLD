package cz.larpovadatabaze.utils;

import cz.larpovadatabaze.Csld;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.Files;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 12.12.12
 * Time: 14:06
 */
public class FileUtils {
    public static String getFileType(String fileName){
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
    public static void cleanFileIfExists(File newFile)
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

    public static String saveFileAndReturnPath(FileUpload upload, String name){
        ServletContext context = ((Csld) Application.get()).getServletContext();
        String realPath = context.getRealPath(Csld.getBaseContext());
        File baseFile = new File(realPath);

        String fileName = Pwd.getMD5(upload.getClientFileName() + name) + "." + FileUtils.getFileType(upload.getClientFileName());
        // Create a new file
        File newFile = new File(baseFile, fileName);

        // Check new file, delete if it already existed
        FileUtils.cleanFileIfExists(newFile);
        try {
            baseFile.mkdirs();
            // Save to new file
            if(!newFile.createNewFile()){
                throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
            }
            upload.writeTo(newFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Csld.getBaseContext() + fileName;
    }
}