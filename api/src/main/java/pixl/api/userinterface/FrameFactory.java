package pixl.api.userinterface;

import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface FrameFactory {
    /**
     * Factory for frames. Can be implemented by an application that wants to produce frames based on its data.
     *
     * @param frame
     * @return
     */
    List<Frame> createFrames(Frame frame);
}
