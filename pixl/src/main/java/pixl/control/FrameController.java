package pixl.control;

import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import pixl.api.application.Application;
import pixl.api.application.data.IconValue;
import pixl.api.application.data.Progress;
import pixl.api.application.data.Value;
import pixl.api.userinterface.Frame;
import pixl.api.userinterface.FrameType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class FrameController {

    private final BiFunction<Value<?>, String, String> formatter;
    private final Application application;
    private final Frame frame;
    private final int frameIndex;

    private final ValueRenderer valueRenderer;
    private final FrameRenderer frameRenderer;

    private transient List<Value<?>> values;

    private transient Image iconImage;
    private transient Image progressImage;
    private transient Image textImage;

    public FrameController(int frameIndex, Frame frame, Application application,
                           BiFunction<Value<?>, String, String> formatter, ValueRenderer valueRenderer) {
        this.frameIndex = frameIndex;
        this.frame = frame;
        this.application = application;
        this.formatter = formatter;
        this.valueRenderer = valueRenderer;
        frameRenderer = new FrameRenderer(frame);
    }

    public void createImage(boolean refresh, int surfaceHeight, int surfaceWidth, Locale locale,
                            Map<String, Object> configuration) {

        progressImage = null;
        textImage = null;
        iconImage = null;

        if (refresh || frame.isGetValueOnRepaint() || values == null) {
            values = application.getValues(configuration);
        }

        int valueIndex = Math.min(frameIndex, values.size() - 1);
        Value<?> value = values.get(valueIndex);
        if (frame.getFrameType() == FrameType.METRIC || frame.getFrameType() == FrameType.PROGRESS || frame
                .getFrameType() == FrameType.TEXT) {
            String text = getText(frame, locale, value).toUpperCase(locale);
            textImage = valueRenderer.renderText(text, surfaceHeight);
        }

        if (frame.getFrameType() == FrameType.PROGRESS && value instanceof Progress) {
            Progress<Number> progress = (Progress) value;

            int availableWidth = surfaceWidth;
            if (iconImage != null) {
                availableWidth -= (iconImage.getWidth(null) + 1);
            }

            progressImage = valueRenderer
                    .renderProgress(progress.getValue(), progress.getMin(), progress.getMax(), availableWidth);
        }

        if (value instanceof IconValue) {
            iconImage = ((IconValue) value).getIcon();
        } else if (StringUtils.isNotBlank(frame.getIcon())) {
            try {
                iconImage = ImageIO.read(Resources.getResource(frame.getIcon()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getText(Frame frame, Locale locale, Value<?> value) {
        String text;
        if (frame.getFormatPattern() != null) {
            String formatPattern = frame.getFormatPattern().getString(locale);
            text = formatter.apply(value, formatPattern);
        } else {
            text = value.getValue().toString();
        }

        if (frame.getSuffix() != null) {
            String suffix = frame.getSuffix().getString(locale);
            if (suffix != null) {
                text += suffix;
            }
        }

        if (frame.getPrefix() != null) {
            String prefix = frame.getPrefix().getString(locale);
            if (prefix != null) {
                text = prefix + text;
            }
        }

        return text;
    }

    public void renderAnimatedImage(Graphics graphics, int height, int width, int x, int y) {
        frameRenderer.setImages(iconImage, progressImage, textImage);
        frameRenderer.renderAnimation(graphics, height, width, x, y);
    }

    public boolean isInFrameAnimation() {
        return frameRenderer.isInFrameAnimation();
    }

    public void setFrameDisplayedDuration(long frameDisplayedDuration) {
        frameRenderer.setFrameDisplayedDuration(frameDisplayedDuration);
    }
}
