package pixl.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@ConfigurationProperties(prefix = "config")
@Component
public class Configuration {
    private String locale;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
