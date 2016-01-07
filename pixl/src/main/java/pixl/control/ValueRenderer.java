package pixl.control;

import pixl.fonts.BitmapFont;
import pixl.fonts.FontRenderer;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class ValueRenderer {

    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color PROGRESS_BG_COLOR = Color.LIGHT_GRAY;
    private final BitmapFont font;

    public ValueRenderer(BitmapFont font) {
        this.font = font;
    }

    public BufferedImage renderText(String text, int height) {
        int width = FontRenderer.getWidth(font, text);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(TEXT_COLOR);

        FontRenderer.drawString(font, 0, 0, text, graphics);
        graphics.dispose();

        return bufferedImage;
    }

    public Image renderProgress(Number value, Number min, Number max, int surfaceWidth) {

        double normalizedValue = value.doubleValue() - min.doubleValue();
        double normalizedMax = max.doubleValue() - min.doubleValue();

        double percent = normalizedValue / normalizedMax;

        BufferedImage bufferedImage = new BufferedImage(surfaceWidth, 1, BufferedImage.TYPE_INT_RGB);

        int fgPixel = (int) Math.round(percent * surfaceWidth);
        int bgPixel = (int) Math.round((1 - percent) * surfaceWidth);

        while (fgPixel + bgPixel > surfaceWidth) {
            if (fgPixel > bgPixel) {
                fgPixel--;
                continue;
            }

            if (bgPixel > fgPixel) {
                bgPixel--;
                continue;
            }
        }

        while (fgPixel + bgPixel < surfaceWidth) {
            if (fgPixel > bgPixel) {
                bgPixel++;
                continue;
            }

            if (bgPixel > fgPixel) {
                fgPixel++;
                continue;
            }
        }

        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(TEXT_COLOR);
        graphics.drawLine(0, 0, fgPixel, 0);
        graphics.setColor(PROGRESS_BG_COLOR);
        graphics.drawLine(fgPixel, 0, fgPixel + bgPixel, 0);
        graphics.dispose();

        return bufferedImage;
    }
}
