package cz.larpovadatabaze.utils;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;
import cz.larpovadatabaze.Csld;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.Files;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
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

    public static String saveImageFileAndReturnPath(FileUpload upload, String name, int maxHeight, int maxWidth){
        ServletContext context = ((Csld) Application.get()).getServletContext();
        String realPath = context.getRealPath(Csld.getBaseContext());
        File baseFile = new File(realPath);

        String fileType = FileUtils.getFileType(upload.getClientFileName());
        String fileName = Pwd.getMD5(upload.getClientFileName() + name) + "." + fileType;
        // Create a new file
        try {

            File newFile = new File(baseFile, fileName);
            ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(maxWidth, maxHeight));
            BufferedImage imageGame = ImageIO.read(upload.getInputStream());
            BufferedImage imageGameSized = resampleOp.filter(imageGame, null);

            // Check new file, delete if it already existed
            FileUtils.cleanFileIfExists(newFile);
            baseFile.mkdirs();
            // Save to new file
            if(!newFile.createNewFile()){
                throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
            }
            ImageIO.write(imageGameSized, fileType, newFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Csld.getBaseContext() + fileName;
    }
}