package pixl.api.application.support;

import com.google.common.collect.ImmutableList;
import pixl.api.application.Application;
import pixl.api.application.data.Value;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface SingleValueApplicationSupport extends Application {

    Value<?> getValue(Map<String, Object> configuration);

    @Override
    default List<Value<?>> getValues(Map<String, Object> configuration) {
        Value<?> value = getValue(configuration);
        if (value != null) {
            return ImmutableList.of(value);
        }
        return ImmutableList.of();
    }
}
