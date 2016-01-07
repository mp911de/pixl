package pixl.plugins.github;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import javaslang.collection.Stream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pixl.api.application.Application;
import pixl.api.application.data.Value;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class GithubStatsApplication implements Application {

    @Inject
    private AsyncRestTemplate restTemplate;

    public final static String API_BASE_URL = "https://api.github.com";

    @org.springframework.beans.factory.annotation.Value("${plugin.github.username}")
    private String username;

    @org.springframework.beans.factory.annotation.Value("${plugin.github.token}")
    private String token;

    private Cache<String, GithubRepoStats> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    public String getId() {
        return "github-repo-stats";
    }

    @Override
    public List<Value<?>> getValues(Map<String, Object> configuration) {

        String repo = (String) configuration.get("repo");
        String assignee = (String) configuration.get("assignee");

        GithubRepoStats stats = cache.getIfPresent(repo);

        if (stats == null) {

            HttpHeaders httpHeaders = new HttpHeaders();
            String auth = username + ":" + token;
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "BASIC " + Base64Utils.encodeToString(auth.getBytes()));
            UriComponentsBuilder issueBuilder = UriComponentsBuilder.fromUriString(API_BASE_URL)
                    .path("repos/" + repo + "/issues");
            UriComponentsBuilder pullsBuilder = UriComponentsBuilder.fromUriString(API_BASE_URL)
                    .path("repos/" + repo + "/pulls");

            if (assignee != null) {
                issueBuilder.queryParam("assignee", assignee);
                pullsBuilder.queryParam("assignee", assignee);
            }

            URI issuesUri = issueBuilder.build().toUri();
            URI pullsUri = pullsBuilder.build().toUri();

            ListenableFuture<ResponseEntity<List<GithubIssue>>> issuesFuture = restTemplate
                    .exchange(issuesUri, HttpMethod.GET, new HttpEntity<Object>(httpHeaders),
                            new ParameterizedTypeReference<List<GithubIssue>>() {
                            });

            ListenableFuture<ResponseEntity<List<GithubIssue>>> pullsFuture = restTemplate
                    .exchange(pullsUri, HttpMethod.GET, new HttpEntity<Object>(httpHeaders),
                            new ParameterizedTypeReference<List<GithubIssue>>() {
                            });

            List<GithubIssue> githubIssues = Stream.ofAll(unchecked(issuesFuture).getBody())
                    .filter(githubIssue -> githubIssue.getPull_request() == null)
                    .toJavaList();

            stats = new GithubRepoStats();
            stats.setIssues(githubIssues);
            stats.setPulls(unchecked(pullsFuture).getBody());
            cache.put(repo, stats);
        }

        Value<String> desc = Value.of(repo);
        Value<Integer> issues = Value.of(stats.getIssues().size());
        Value<Integer> pulls = Value.of(stats.getPulls().size());
        return ImmutableList.of(desc, issues, pulls);
    }

    private ResponseEntity<List<GithubIssue>> unchecked(
            ListenableFuture<ResponseEntity<List<GithubIssue>>> future) {
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
