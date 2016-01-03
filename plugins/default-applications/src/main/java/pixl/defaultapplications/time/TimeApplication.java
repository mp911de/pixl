package pixl.defaultapplications.time;

import com.google.common.collect.ImmutableList;
import pixl.api.application.Application;
import pixl.api.application.data.Value;

import java.time.Clock;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Returns the current time.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class TimeApplication implements Application {

    private Clock clock = Clock.systemDefaultZone();

    @Override
    public String getId() {
        return "time";
    }

    @Override
    public List<Value<?>> getValues(Map<String, Object> configuration) {
        return ImmutableList.of(Value.of(Date.from(clock.instant())));
    }
}
