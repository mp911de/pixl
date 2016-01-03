package pixl.fonts;

import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class BitmapFontParser {

    public static final String GLYPHS_PROPERTIES = "/glyphs.properties";
    private final Font font;
    private final String resourcePrefix;

    public BitmapFontParser(Font font, String resourcePrefix) {
        this.font = font;
        this.resourcePrefix = resourcePrefix;
    }

    public BitmapFont parse() throws IOException {

        Properties properties = getGlyphMapping();
        Map<String, String> glyphMapping = Maps.fromProperties(properties);

        char[] charset = font.getCharset();

        Map<Character, Glyph> glyphs = Maps.newHashMap();

        for (char c : charset) {

            String string = new String(new char[]{c}).toLowerCase();
            String glyphName = glyphMapping.getOrDefault(string, string);
            URL resource = Resources.getResource(resourcePrefix + "/" + glyphName + ".glyph");
            GlyphParser glyphParser = new GlyphParser(resource);

            Glyph glyph = glyphParser.parse();
            glyphs.put(c, glyph);
        }

        return new BitmapFont(font, glyphs);
    }

    private Properties getGlyphMapping() throws IOException {
        URL glyphMappingResource = getClass().getClassLoader().getResource(resourcePrefix + GLYPHS_PROPERTIES);
        Properties properties = new Properties();

        if (glyphMappingResource != null) {
            try (InputStream is = glyphMappingResource.openStream()) {
                properties.load(is);
            }
        }
        return properties;
    }
}
