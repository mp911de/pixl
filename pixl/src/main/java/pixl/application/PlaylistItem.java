package pixl.application;

import lombok.Data;

import java.time.Duration;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
public class PlaylistItem {
    private String application;
    private Duration duration;
    private Map<String, Object> configuration;
}
