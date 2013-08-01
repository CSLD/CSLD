package cz.larpovadatabaze.utils;

import org.apache.wicket.util.file.Files;

import java.io.File;

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
}
