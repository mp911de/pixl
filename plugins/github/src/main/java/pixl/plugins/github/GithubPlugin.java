package pixl.plugins.github;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import pixl.api.application.Application;
import pixl.api.application.HasApplications;
import pixl.api.plugin.Plugin;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class GithubPlugin implements HasApplications, Plugin{

    @Inject
    private GithubStatsApplication githubStatsApplication;

    @Override
    public Collection<Application> getApplications() {
        return ImmutableList.of(githubStatsApplication);
    }
}
