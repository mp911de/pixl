package pixl.fonts;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class FontRendererTest {

    @Test
    public void drawAllChars() throws Exception {

        BitmapFontParser parser = new BitmapFontParser(new DefaultFont(), DefaultFont.RESOURCE_PREFIX);
        BitmapFont font = parser.parse();

        int lineBreakAfter = 10;
        char[] charset = font.getCharset();
        int lines = (int) Math.floor(charset.length / (double) lineBreakAfter) + 1;

        int x = 0;
        int y = 0;

        int lineHeight = 6;
        BufferedImage image = new BufferedImage(5 * 10, lines * lineHeight, BufferedImage.TYPE_INT_RGB);

        String theString = "";
        Graphics2D graphics = image.createGraphics();
        for (int i = 0; i < charset.length; i++) {

            if (i != 0 && i % lineBreakAfter == 0) {

                FontRenderer.drawString(font, x, y, theString, graphics);
                y += lineHeight;
                theString = "";
            }

            theString += charset[i];
        }

        if (!theString.isEmpty()) {
            FontRenderer.drawString(font, x, y, theString, graphics);
        }

        graphics.dispose();

        ImageIO.write(image, "png", new File("build/charset.png"));
    }
}