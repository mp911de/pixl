package pixl.plugin.travisci;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.net.HttpHeaders;
import javaslang.Tuple2;
import javaslang.collection.Stream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pixl.api.application.data.IconValue;
import pixl.api.application.data.Value;
import pixl.api.application.support.SingleValueApplicationSupport;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@org.springframework.stereotype.Component
public class TravisCiSummary implements SingleValueApplicationSupport {

    public final static Color IN_PROGRESS = hex2Rgb("#E5DC29");
    public final static Color SUCESS = hex2Rgb("#3FA75F");
    public final static Color FAILED = hex2Rgb("#DB423C");
    public final static Color UNKNOWN = Color.GRAY;

    private Cache<String, List<TravisCiBuild>> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public final static String API_BASE_URL = "https://api.travis-ci.org";

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    @org.springframework.beans.factory.annotation.Value("${plugin.travisci.token:}")
    private String token;

    @Override
    public String getId() {
        return "travis-ci-summary";
    }

    @Override
    public Value<?> getValue(Map<String, Object> configuration) {
        if (configuration.get("repositories") instanceof Collection) {
            List<String> repositories = (List) configuration.get("repositories");

            Stream<Tuple2<String, ListenableFuture<ResponseEntity<List<TravisCiBuild>>>>> map = Stream
                    .ofAll(repositories).take(8).filter(repo -> cache.getIfPresent(repo) == null).map(
                            repo -> {
                                UriComponents uriComponents = UriComponentsBuilder.fromUriString(API_BASE_URL)
                                        .path("repos/")
                                        .path(repo)
                                        .path("/builds").build();

                                HttpEntity httpEntity;
                                if (StringUtils.hasText(token)) {
                                    org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
                                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "token \"" + token + "\"");

                                    httpEntity = new HttpEntity<>(null, httpHeaders);
                                } else {
                                    httpEntity = HttpEntity.EMPTY;
                                }

                                ListenableFuture<ResponseEntity<List<TravisCiBuild>>> exchange = asyncRestTemplate
                                        .exchange(uriComponents.toUri(), HttpMethod.GET,
                                                httpEntity, new ParameterizedTypeReference<List<TravisCiBuild>>() {

                                                });

                                return new Tuple2<>(repo, exchange);
                            }
                    );

            List<Tuple2<String, List<TravisCiBuild>>> results = map
                    .filter(t -> unchecked(t._2).getStatusCode().is2xxSuccessful())
                    .map(t -> new Tuple2<>(t._1, unchecked(t._2).getBody())).toJavaList();

            for (int i = 0; i < results.size(); i++) {
                Tuple2<String, List<TravisCiBuild>> tuple = results.get(i);
                cache.put(tuple._1, tuple._2);
            }

            BufferedImage icon = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);

            int success = 0;
            int failed = 0;
            int inProgress = 0;
            int pixelWidth = 1;
            int pixelHeight = 1;

            if (repositories.size() == 4) {
                pixelWidth = 2;
                pixelHeight = 2;
            }

            if (repositories.size() == 2) {
                pixelWidth = 4;
                pixelHeight = 2;
            }

            for (int x = 0; x < repositories.size(); x++) {
                List<TravisCiBuild> builds = cache.getIfPresent(repositories.get(x));
                if (builds == null) {
                    continue;
                }

                for (int y = 0; y < 8 / pixelHeight; y++) {
                    TravisCiBuild build = null;
                    if (builds.size() > y) {
                        build = builds.get(y);
                    }

                    int color = UNKNOWN.getRGB();
                    if (build != null) {

                        if ("started".equals(build.state)) {
                            color = IN_PROGRESS.getRGB();
                            if (y == 0) {
                                inProgress++;
                            }
                        }

                        if ("finished".equals(build.state)) {

                            if (build.result != null && build.result.intValue() == 0) {
                                color = SUCESS.getRGB();
                                success++;
                            } else {
                                color = FAILED.getRGB();

                                if (y == 0) {
                                    failed++;
                                }
                            }
                        }
                    }
                    for (int h = 0; h < pixelHeight; h++)
                        for (int w = 0; w < pixelWidth; w++)
                            icon.setRGB(Math.min((x * pixelWidth) + w, icon.getWidth() - 1),
                                    Math.min((y * pixelHeight) + h, icon.getHeight() - 1), color);
                }
            }

            return IconValue.of(success + " S " + inProgress + " P " + failed + " E", icon);
        }

        return null;
    }

    private ResponseEntity<List<TravisCiBuild>> unchecked(
            ListenableFuture<ResponseEntity<List<TravisCiBuild>>> future) {
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
