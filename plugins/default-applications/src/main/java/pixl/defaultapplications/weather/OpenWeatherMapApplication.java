package pixl.defaultapplications.weather;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pixl.api.application.Application;
import pixl.api.application.data.IconValue;
import pixl.api.application.data.Value;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Application to retrieve weather status from openweathermap.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Service
public class OpenWeatherMapApplication implements Application {

    @Inject
    private RestTemplate restTemplate;

    private String apiEndpoint = "http://api.openweathermap.org/data/2.5/weather";

    @org.springframework.beans.factory.annotation.Value("${apps.openweathermap.apikey}")
    private String apiKey;

    @org.springframework.beans.factory.annotation.Value("${apps.openweathermap.enabled}")
    private boolean enabled;

    private IconValue<String> iconValue;

    @Override
    public String getId() {
        return "weather";
    }

    @PostConstruct
    public void postConstruct() {
        if (enabled) {
            checkState(StringUtils.hasText(apiKey) && apiKey.indexOf('$') == -1, "openweathermap.apikey missing");
        }
    }

    @Override
    public List<Value<?>> getValues(Map<String, Object> configuration) {

        if (!enabled) {
            return ImmutableList.of(Value.of(getId() + " not enabled"));
        }

        // TODO: Caching
        if (iconValue != null) {
            return ImmutableList.of(iconValue);
        }

        String location = (String) configuration.getOrDefault("location", "London,UK");
        String unit = (String) configuration.getOrDefault("unit", "c");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiEndpoint);
        UriComponents uriComponents = builder.queryParam("appid", apiKey).queryParam("q", location).build();

        Map<String, Object> weatherData = restTemplate.getForObject(uriComponents.toUri(), Map.class);

        List<Map<String, Object>> weather = (List) weatherData.get("weather");

        String icon = getIcon(weather);
        String text = getText(unit, weatherData);

        Value<?> value = Value.of("?: " + location);
        try {
            if (text != null && icon != null) {
                BufferedImage image = ImageIO.read(Resources.getResource("weather/" + icon + ".gif"));
                value = iconValue = IconValue.of(text, image);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return ImmutableList.of(value);
    }

    private String getText(String unit, Map<String, Object> weatherData) {
        Map<String, Object> main = (Map<String, Object>) weatherData.get("main");
        String text = null;
        if (main != null) {
            double k = (Double) main.get("temp");

            double c = k - 273.15;
            double f = k * 9 / 5 - 459.67;

            if ("C".equalsIgnoreCase(unit)) {
                text = ((int) c) + "°C";
            } else {
                text = ((int) f) + "°F";
            }
        }
        return text;
    }

    private String getIcon(List<Map<String, Object>> weather) {
        String icon = "sunny";
        if (weather != null && !weather.isEmpty()) {
            Map<String, Object> weatherDesc = weather.get(0);
            String main = (String) weatherDesc.get("main");
            if (main != null) {
                if ("clouds".equalsIgnoreCase(main)) {
                    icon = "cloud";
                }
                if ("rain".equalsIgnoreCase(main)) {
                    icon = "rain";
                }
                if ("snow".equalsIgnoreCase(main)) {
                    icon = "cloud";
                }

                if ("clear".equalsIgnoreCase(main)) {
                    icon = "sunny";
                }

                if ("drizzle".equalsIgnoreCase(main)) {
                    icon = "drizzle";
                }
            }
        }
        return icon;
    }

}
