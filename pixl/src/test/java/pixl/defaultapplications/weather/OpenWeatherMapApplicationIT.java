package pixl.defaultapplications.weather;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.web.client.RestTemplate;

import static org.fest.reflect.core.Reflection.field;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class OpenWeatherMapApplicationIT {

    private OpenWeatherMapApplication sut;

    @Before
    public void before() throws Exception {
        sut = new OpenWeatherMapApplication();
        field("apiKey").ofType(String.class).in(sut).set("912e531d6848c55fd6bfba4239b45fcb");
        field("restTemplate").ofType(RestTemplate.class).in(sut).set(new TestRestTemplate());

        sut.postConstruct();
    }

    @Test
    public void testName() throws Exception {
        sut.getValues(ImmutableMap.of("location", "Weinheim,DE"));

    }
}