package cz.larpovadatabaze.services;

import java.awt.image.BufferedImage;

/**
 * Factory for creating image resizing strategies
 *
 * User: Michal Kara
 * Date: 1.1.14
 * Time: 13:18
 */
public interface ImageResizingStrategyFactoryService {
    public static interface IImageResizingStrategy {
        /**
         * Convert image using the strategy
         *
         * @param sourceImage Source image
         *
         * @return Output image
         */
        public BufferedImage convertImage(BufferedImage sourceImage);
    }

    /**
     * Get strategy that resizes image so it fits maximum bounds, pixel ratio is unchanged
     *
     * @param maxWidth Maximum width
     * @param maxHeight Maximum height
     *
     * @return Strategy that will convert image using specified parameters
     */
    public IImageResizingStrategy getMaxWidthHeightStrategy(int maxWidth, int maxHeight);

    /**
     * Get strategy which cuts square from the image, as big as possible and then resize the square to
     * the given size. When original image is no
     *
     * @param sideSize Size of one side of the square
     * @param leftTopPercent Percentage (0-100) of left out space to be placed left (or on top) of the cutted square. Simply said, when we have
     *                       image that is wider than taller, value 0 here means the resulting square will be aligned with the left side of the original image,
     *                       value 100 means it will be aligned with the right side, value 50 means it will be in the center. Etc.
     *
     * @return Strategy that will convert image using specified parameters
     */
    public IImageResizingStrategy getCuttingSquareStrategy(int sideSize, float leftTopPercent);
}
