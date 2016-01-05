package pixl.application;

import lombok.Data;

import java.time.Duration;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
public class Frame {
    private String icon;
    private FrameType frameType;
    private Localized prefix;
    private Localized suffix;
    private Datatype datatype;
    private Duration duration;
    private boolean active = true;
    private boolean getValueOnRepaint = false;
    private Localized formatPattern;
}
