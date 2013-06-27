package cz.larpovadatabaze.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 13.12.12
 * Time: 15:30
 */
public class ImageUtil {
    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();

        return bufferedImage;
    }

    public static int[] getDimensions(Image image){
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int width =(int) d.getWidth();
        int height =(int) d.getHeight();
        return new int[]{width, height};
    }

}
