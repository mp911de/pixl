package pixl.plugin.travisci;

import org.springframework.stereotype.Component;
import pixl.api.application.Application;
import pixl.api.application.HasApplications;
import pixl.api.application.support.SingleApplicationSupport;
import pixl.api.plugin.Plugin;

import javax.inject.Inject;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class TravisCiPlugin implements SingleApplicationSupport, Plugin {

    @Inject
    private TravisCiSummary summary;

    @Override
    public Application getApplication() {
        return summary;
    }
}
