package pixl.api.application;

import pixl.api.userinterface.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class ApplicationRegistrySupport {

    public static final String KEY_ACTIVE = "active";
    public static final String KEY_FRAMES = "frames";
    public static final String KEY_ICON = "icon";
    public static final String KEY_FRAME_TYPE = "type";
    public static final String KEY_PREFIX = "prefix";
    public static final String KEY_SUFFIX = "suffix";
    public static final String KEY_DATATYPE = "datatype";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_FORMAT_PATTERN = "formatPattern";
    public static final String KEY_GET_VALUE_ON_REPAINT = "getValueOnRepaint";

    /**
     * Read application frames from a config map.
     * @param applicationData
     * @param applicationFrames
     */
    public static void readApplicationData(Map<String, Object> applicationData, ApplicationFrames applicationFrames) {

        if (applicationData.containsKey(KEY_ACTIVE)) {
            applicationFrames.setActive((Boolean) applicationData.get(KEY_ACTIVE));
        }

        if (applicationData.containsKey(KEY_FRAMES)) {
            List<Map<String, Object>> framesData = (List<Map<String, Object>>) applicationData.get(KEY_FRAMES);
            List<Frame> frames = applicationFrames.getFrames();

            for (int i = 0; i < framesData.size(); i++) {
                Map<String, Object> frameData = framesData.get(i);
                Frame frame;
                if (frames.size() > i) {
                    frame = frames.get(i);
                } else {
                    frame = new Frame();
                    frames.add(frame);
                }

                readFrameData(frameData, frame);
            }
        }
    }

    private static void readFrameData(Map<String, Object> frameData, Frame frame) {
        if (frameData.containsKey(KEY_ICON)) {
            frame.setIcon((String) frameData.get(KEY_ICON));
        }

        if (frameData.containsKey(KEY_FRAME_TYPE)) {
            frame.setFrameType(FrameType.valueOf(((String) frameData.get(KEY_FRAME_TYPE)).toUpperCase()));
        }

        if (frameData.containsKey(KEY_DATATYPE)) {
            frame.setDatatype(Datatype.valueOf(((String) frameData.get(KEY_DATATYPE)).toUpperCase()));
        }

        if (frameData.containsKey(KEY_PREFIX)) {
            frame.setPrefix(toLocalized(frame.getPrefix(), frameData.get(KEY_PREFIX)));
        }

        if (frameData.containsKey(KEY_SUFFIX)) {
            frame.setSuffix(toLocalized(frame.getSuffix(), frameData.get(KEY_SUFFIX)));
        }

        if (frameData.containsKey(KEY_ACTIVE)) {
            frame.setActive((Boolean) frameData.get(KEY_ACTIVE));
        }

        if (frameData.containsKey(KEY_GET_VALUE_ON_REPAINT)) {
            frame.setGetValueOnRepaint((Boolean) frameData.get(KEY_GET_VALUE_ON_REPAINT));
        }

        if (frameData.containsKey(KEY_DURATION)) {
            frame.setDuration(Duration.parse((String) frameData.get(KEY_DURATION)));
        }

        if (frameData.containsKey(KEY_FORMAT_PATTERN)) {
            frame.setFormatPattern(toLocalized(frame.getFormatPattern(), frameData.get(KEY_FORMAT_PATTERN)));
        }
    }

    private static Localized toLocalized(Localized localized, Object o) {

        if (localized == null) {
            localized = new Localized();
        }

        if (o instanceof String) {
            localized.setDefaultText((String) o);
        }

        if (o instanceof Map) {
            Map<String, String> map = (Map) o;
            localized.getText().putAll(map);
        }

        return localized;
    }
}
