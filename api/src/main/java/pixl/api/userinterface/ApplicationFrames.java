package pixl.api.userinterface;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
public class ApplicationFrames {
    private String id;
    private List<Frame> frames = Lists.newArrayList();
    private boolean active = true;

    public int getFrameCount() {
        return frames.size();
    }

    public Frame getFrame(int frameIndex) {
        return frames.get(frameIndex);
    }
}
