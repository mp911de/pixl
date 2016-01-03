package pixl.fonts;

import com.google.common.io.Resources;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class GlyphParserTest {
    private GlyphParser sut;

    @Test
    public void successful() throws Exception {

        sut = new GlyphParser(Resources.getResource("fonts/default/exclamation.glyph"));

        Glyph parse = sut.parse();

        assertThat(parse.getHeight()).isEqualTo(5);
        assertThat(parse.getWidth()).isEqualTo(1);
        assertThat(parse.getYxData().length).isEqualTo(5);

        assertThat(parse.getYxData()[0]).isEqualTo(new boolean[]{true});
        assertThat(parse.getYxData()[3]).isEqualTo(new boolean[]{false});
        assertThat(parse.getYxData()[4]).isEqualTo(new boolean[]{true});
    }

    @Test(expected = IllegalStateException.class)
    public void broken() throws Exception {
        sut = new GlyphParser(Resources.getResource("broken.glyph"));
        sut.parse();
    }
}