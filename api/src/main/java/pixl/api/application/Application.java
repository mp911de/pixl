package pixl.api.application;

import pixl.api.application.data.Value;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface Application {

    /**
     * @return the application Id.
     */
    String getId();

    /**
     * Get values for all frames.
     *
     * @param configuration
     * @return the data.
     */
    List<Value<?>> getValues(Map<String, Object> configuration);
}
