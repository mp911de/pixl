package pixl.infrastructure;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Configuration
public class HttpClientConfiguration {

    @Value("${http.client.connectTimeoutMs:5000}")
    private Integer connectTimeoutMs;

    @Value("${http.client.requestTimeoutMs:20000}")
    private Integer requestTimeoutMs;

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return HttpClientBuilder.create().build();
    }

    @Bean
    public CloseableHttpAsyncClient closeableHttpAsyncClient() {
        return HttpAsyncClients.createSystem();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                closeableHttpClient());

        setTimeouts(factory);
        return factory;
    }

    @Bean
    public AsyncClientHttpRequestFactory asyncClientHttpRequestFactory() {
        HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory(
                closeableHttpAsyncClient());

        setTimeouts(factory);
        return factory;
    }

    public void setTimeouts(HttpComponentsClientHttpRequestFactory factory) {
        factory.setConnectionRequestTimeout(requestTimeoutMs);
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(requestTimeoutMs);
    }

    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate(AsyncClientHttpRequestFactory clientHttpRequestFactory) {
        return new AsyncRestTemplate(clientHttpRequestFactory);
    }
}
