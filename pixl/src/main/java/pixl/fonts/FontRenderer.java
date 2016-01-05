package pixl.fonts;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class FontRenderer {

    public static int getWidth(BitmapFont font, String text) {

        int result = 0;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            if (i != 0) {
                result += font.getLetterSpacing();
            }

            result += font.getGlyph(chars[i]).getWidth();
        }

        return result;
    }

    /**
     * Draw a string on the graphics.
     *
     * @param font
     * @param x
     * @param y
     * @param text
     * @param g
     */
    public static void drawString(BitmapFont font, int x, int y, String text, Graphics g) {
        char[] chars = text.toUpperCase().toCharArray();
        int currentX = x;
        for (int i = 0; i < chars.length; i++) {

            if (i != 0) {
                currentX += font.getLetterSpacing();
            }

            Glyph glyph = font.getGlyph(chars[i]);
            BufferedImage image = new BufferedImage(glyph.getWidth(), glyph.getHeight(), BufferedImage.TYPE_INT_RGB);

            int topOffset = font.getMinimumHeight() - glyph.getHeight();
            boolean[][] yxData = glyph.getYxData();
            for (int dx = 0; dx < glyph.getWidth(); dx++) {
                for (int dy = 0; dy < glyph.getHeight(); dy++) {
                    if (yxData[dy][dx]) {
                        image.setRGB(dx, dy, g.getColor().getRGB());
                    }
                }
            }
            g.drawImage(image, currentX, topOffset + y, null);
            currentX += glyph.getWidth();
        }
    }

}
