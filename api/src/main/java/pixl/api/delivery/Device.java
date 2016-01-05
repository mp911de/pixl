package pixl.api.delivery;

import com.sun.org.apache.xpath.internal.operations.Bool;
import rx.Observable;

import javax.sound.sampled.AudioInputStream;
import java.awt.*;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface Device {

    /**
     * Pushes an image on the device.
     *
     * @param image
     */
    void pushImage(Image image);

    /**
     * Push a sound to the device.
     *
     * @param audioInputStream
     */
    void pushSound(AudioInputStream audioInputStream);

    /**
     * Subscribe to the trigger button.
     *
     * @return
     */
    Observable<Boolean> triggerButton();

}
