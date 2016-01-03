package pixl.application;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
public class Localized {
    private Map<String, String> text = Maps.newHashMap();
    private String defaultText;

    public String getString(Locale locale) {

        return text.computeIfAbsent(locale.toString(), (k1) -> text.computeIfAbsent(locale.getCountry(),
                (k2) -> text.computeIfAbsent(locale.getLanguage(), k3 -> defaultText)));

    }
}
