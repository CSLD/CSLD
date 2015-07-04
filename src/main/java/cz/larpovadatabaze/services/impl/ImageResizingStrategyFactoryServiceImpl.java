package cz.larpovadatabaze.services.impl;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;

/**
 * User: Michal Kara
 * Date: 1.1.14
 * Time: 15:11
 */
@Service
public class ImageResizingStrategyFactoryServiceImpl implements ImageResizingStrategyFactoryService {
    @Override
    public IImageResizingStrategy getMaxWidthHeightStrategy(final int maxWidth, final int maxHeight) {
        return new IImageResizingStrategy() {
            @Override
            public BufferedImage convertImage(BufferedImage sourceImage) {
                ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(maxWidth, maxHeight, true));
                return resampleOp.filter(sourceImage, null);
            }
        };
    }

    @Override
    public IImageResizingStrategy getCuttingSquareStrategy(final int sideSize, final float leftTopPercent) {
        if ((leftTopPercent < 0) || (leftTopPercent > 100)) throw new IllegalArgumentException("Invalid percentage");

        return new IImageResizingStrategy() {
            @Override
            public BufferedImage convertImage(BufferedImage sourceImage) {
                // Compute source
                int sx1,sy1,sx2,sy2;
                int sw = sourceImage.getWidth();
                int sh = sourceImage.getHeight();
                if (sw > sh) {
                    sy1 = 0;
                    sy2 = sh;
                    sx1 = Math.round((sw-sh)*leftTopPercent/100);
                    sx2 = sx1+sh;
                }
                else {
                    sx1 = 0;
                    sx2 = sw;
                    sy1 = Math.round((sh-sw)*leftTopPercent/100);
                    sy2 = sy1 + sw;
                }

                // Create, copy and save image
                BufferedImage previewImage = new BufferedImage(sideSize, sideSize, BufferedImage.TYPE_3BYTE_BGR);
                previewImage.getGraphics().drawImage(sourceImage, 0, 0, sideSize, sideSize, sx1, sy1, sx2, sy2, null);
                return previewImage;
            }
        };
    }

    @Override
    public IImageResizingStrategy getCoverImageStrategy() {
        return new IImageResizingStrategy() {
            private static final double REQUIRED_RATIO = 6d;

            @Override
            public BufferedImage convertImage(BufferedImage sourceImage) {
                int sw = sourceImage.getWidth();
                int sh = sourceImage.getHeight();

                if ((((double)sw)/sh) >= REQUIRED_RATIO) {
                    // Image is shorter than needed - use source image
                    return sourceImage;
                }

                // Cut image
                int resH = (int)(((double)sw)/REQUIRED_RATIO);

                BufferedImage previewImage = new BufferedImage(sw, resH, BufferedImage.TYPE_3BYTE_BGR);
                previewImage.getGraphics().drawImage(sourceImage, 0, 0, sw, resH, 0, 0, sw, resH, null);
                return previewImage;
            }
        };
    }
}
