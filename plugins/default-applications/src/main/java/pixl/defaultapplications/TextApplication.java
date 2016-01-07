package pixl.defaultapplications;

import pixl.api.application.data.Value;
import pixl.api.application.support.SingleValueApplicationSupport;

import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class TextApplication implements SingleValueApplicationSupport {
    @Override
    public String getId() {
        return "text";
    }

    @Override
    public Value<?> getValue(Map<String, Object> configuration) {
        return Value.of(configuration.get("text"));
    }
}
