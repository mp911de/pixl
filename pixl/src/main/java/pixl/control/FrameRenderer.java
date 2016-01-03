package pixl.control;

import pixl.application.Frame;
import pixl.application.FrameType;

import java.awt.*;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class FrameRenderer {

    private final Frame frame;

    private Image iconImage;
    private Image progressImage;
    private Image textImage;

    private boolean inFrameAnimation;
    private boolean repeatAnimation = true;
    private long frameDisplayedDuration;

    public FrameRenderer(Frame frame) {
        this.frame = frame;
    }

    public void setImages(Image iconImage, Image progressImage, Image textImage) {
        this.iconImage = iconImage;
        this.progressImage = progressImage;
        this.textImage = textImage;
    }

    public void setRepeatAnimation(boolean repeatAnimation) {
        this.repeatAnimation = repeatAnimation;
    }

    public void setFrameDisplayedDuration(long frameDisplayedDuration) {
        this.frameDisplayedDuration = frameDisplayedDuration;
    }

    public boolean isInFrameAnimation() {
        return inFrameAnimation;
    }

    public void renderAnimation(Graphics graphics, int height, int width, int x, int y) {

        int availableWidth = width;
        if (iconImage != null) {
            int iconSpace = (iconImage.getWidth(null) + 1);
            availableWidth -= iconSpace;

            graphics.drawImage(iconImage, x, y, null);
            x += iconSpace;
        }

        if (frame.getFrameType() == FrameType.METRIC || frame.getFrameType() == FrameType.TEXT || frame
                .getFrameType() == FrameType.PROGRESS) {

            int textImageWidth = textImage.getWidth(null);
            if (availableWidth > textImageWidth) {
                int leftX = availableWidth / 2 - textImageWidth / 2;
                graphics.drawImage(textImage, x + leftX, y, null);
                inFrameAnimation = false;
            } else {
                inFrameAnimation = true;

                int overflow = textImageWidth - availableWidth;
                long displayLengthMs = Animation.PRE_POST_ROLL_DISPLAY_LENGTH;
                double scrollDuration = (overflow / Animation.scrollPixelPerSecond) * 1000;

                long animationPhase = (displayLengthMs * 2) + (long) scrollDuration;

                long full = (frameDisplayedDuration / animationPhase);
                double ms = ((frameDisplayedDuration / (double) animationPhase) - full) * (double) animationPhase;
                if (!repeatAnimation) {
                    ms = animationPhase;
                }

                if (ms > displayLengthMs) {

                    if (ms > (displayLengthMs + scrollDuration)) {
                        ms = displayLengthMs + scrollDuration;
                        inFrameAnimation = false;
                    }

                    int scrollOffsetX = (int) (((ms - displayLengthMs) / scrollDuration) * overflow);
                    graphics.drawImage(textImage, x, y, width, y + height, x + scrollOffsetX, 0,
                            x + scrollOffsetX + availableWidth, textImage.getHeight(null), null);

                } else {
                    graphics.drawImage(textImage, x, y, null);
                }
            }
        }

        if (frame
                .getFrameType() == FrameType.PROGRESS) {
            graphics.drawImage(progressImage, width - availableWidth, y + height - 1, null);
        }
    }
}
