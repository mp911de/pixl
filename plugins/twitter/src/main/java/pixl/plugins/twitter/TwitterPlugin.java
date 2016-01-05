package pixl.plugins.twitter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;
import pixl.api.application.Application;
import pixl.api.application.HasApplications;
import pixl.api.application.data.Value;
import pixl.api.plugin.Plugin;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class TwitterPlugin implements Application, HasApplications, Plugin {

    @Inject
    private Twitter twitter;

    private Cache<String, List<Tweet>> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    public String getId() {
        return "twitter";
    }

    @Override
    public List<Value<?>> getValues(Map<String, Object> configuration) {

        List<Tweet> homeTimeline = cache.getIfPresent("default");

        if (homeTimeline == null) {
            homeTimeline = twitter.timelineOperations().getHomeTimeline(2);
            cache.put("default", homeTimeline);
        }

        if (!homeTimeline.isEmpty()) {
            Tweet tweet = homeTimeline.get(0);
            return ImmutableList.of(Value.of("@" + tweet.getFromUser() + ": " + tweet.getText()));
        }

        return ImmutableList.of(Value.of("EMPTY"));
    }

    @Override
    public Collection<Application> getApplications() {
        return ImmutableList.of((Application) this);
    }
}
