package pixl.fonts;

import com.google.common.collect.Lists;
import javaslang.collection.Stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse a {@code .glyph} file.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class GlyphParser {

    private URL resource;

    public GlyphParser(URL resource) {
        this.resource = resource;
    }

    /**
     * Parse a glyph. Empty lines and comment lines (#) are skipped. Every line has to have the same length. Whitespaces
     * are trimmed. The draw-marker for a bit is {@code 1}.
     *
     * @return
     * @throws IOException
     */
    public Glyph parse() throws IOException {

        int width = 0;
        int height = 0;
        List<Boolean[]> data = Lists.newArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()))) {

            int currentLine = 0;
            List<String> lines = reader.lines().collect(Collectors.toList());

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.equals("") || line.startsWith("#")) {
                    continue;
                }

                if (currentLine == 0) {
                    width = line.length();
                } else if (line.length() != width) {
                    throw new IllegalStateException(
                            "Length mismatch in line " + i + ". Expected " + width + ", actual length " + line
                                    .length() + " (" + line + ") in " + resource);
                }

                data.add(toBits(line));
                currentLine++;
            }
        }

        height = data.size();

        boolean[][] glyphData = new boolean[height][];

        for (int y = 0; y < height; y++) {

            Boolean[] booleans = data.get(y);
            glyphData[y] = new boolean[width];
            for (int x = 0; x < width; x++) {
                glyphData[y][x] = booleans[x];
            }
        }

        return new Glyph(width, height, glyphData);
    }

    private Boolean[] toBits(String line) {
        char[] input = line.toCharArray();
        return Stream.ofAll(input).map(character -> character == '1').toJavaArray(Boolean.class);
    }
}
