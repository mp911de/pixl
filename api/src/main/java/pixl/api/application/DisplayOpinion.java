package pixl.api.application;

import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface DisplayOpinion {
    /**
     * Opinionated application that indicates whether it wants to be displayed or not.
     *
     * @return {@literal true} if the application wants to be displayed
     */
    boolean shouldDisplay(Map<String, Object> configuration);
}
