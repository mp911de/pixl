package pixl.fonts;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class DefaultFont implements Font {

    public final static char[] CHARSET = new char[]{
            // digits
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            // A-Z
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            // special chars
            ' ', '.', ':', '°', '+', '-', '_', '*', '=', '/', '%', '!', '?', '>', '<', '(', ')', '[', ']'};

    public final static char FALLBACK = '?';

    public final static String RESOURCE_PREFIX = "fonts/default";

    @Override
    public char[] getCharset() {
        return CHARSET;
    }

    @Override
    public char getFallback() {
        return FALLBACK;
    }
}
