package pixl.fonts;

import lombok.Data;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */

@Data
public class Glyph {
    private final int width;
    private final int height;

    // y/x
    private final boolean[][] yxData;

    public Glyph(int width, int height, boolean[][] yxData) {
        this.width = width;
        this.height = height;
        this.yxData = yxData;
    }
}
