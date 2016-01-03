package pixl.fonts;

import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class BitmapFont implements Font {

    private final Font font;
    private final Map<Character, Glyph> glyphs;

    public BitmapFont(Font font, Map<Character, Glyph> glyphs) {
        this.font = font;
        this.glyphs = glyphs;
    }

    @Override
    public char[] getCharset() {
        return font.getCharset();
    }

    @Override
    public char getFallback() {
        return font.getFallback();
    }

    public Glyph getGlyph(char character) {
        return glyphs.computeIfAbsent(character, ch -> glyphs.get(font.getFallback()));
    }

    public int getMinimumHeight() {
        return 6;
    }

    public int getLetterSpacing() {
        return 1;
    }
}
