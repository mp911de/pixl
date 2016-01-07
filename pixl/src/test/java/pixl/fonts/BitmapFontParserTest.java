package pixl.fonts;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class BitmapFontParserTest {

    @Test
    public void parseDefaultFont() throws Exception {
        BitmapFontParser parser = new BitmapFontParser(new DefaultFont(), DefaultFont.RESOURCE_PREFIX);
        BitmapFont font = parser.parse();

        assertThat(font.getGlyph('q')).isNotNull();
        assertThat(font.getGlyph('(')).isNotNull();

        assertThat(font.getGlyph('M').getWidth()).isEqualTo(5);
    }
}