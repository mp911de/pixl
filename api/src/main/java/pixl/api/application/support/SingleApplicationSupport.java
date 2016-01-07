package pixl.api.application.support;

import com.google.common.collect.ImmutableList;
import pixl.api.application.Application;
import pixl.api.application.HasApplications;

import java.util.Collection;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface SingleApplicationSupport extends HasApplications {

    Application getApplication();

    @Override
    default Collection<Application> getApplications() {
        return ImmutableList.of(getApplication());
    }
}
