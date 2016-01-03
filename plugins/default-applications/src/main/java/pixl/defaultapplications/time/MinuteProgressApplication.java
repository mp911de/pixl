package pixl.defaultapplications.time;

import com.google.common.collect.ImmutableList;
import pixl.api.application.Application;
import pixl.api.application.data.Progress;
import pixl.api.application.data.Value;

import java.time.Clock;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

/**
 * Returns the time progress of a minute.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class MinuteProgressApplication implements Application {

    private Clock clock = Clock.systemDefaultZone();

    @Override
    public String getId() {
        return "minute";
    }

    @Override
    public List<Value<?>> getValues(Map<String, Object> configuration) {

        LocalTime now = LocalTime.now(clock);

        long second = now.get(ChronoField.SECOND_OF_MINUTE);
        return ImmutableList.of(Progress.of(second, 0, 59));
    }
}
