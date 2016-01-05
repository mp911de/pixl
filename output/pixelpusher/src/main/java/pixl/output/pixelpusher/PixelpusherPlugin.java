package pixl.output.pixelpusher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.heroicrobot.dropbit.devices.pixelpusher.PixelPusher;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;
import com.heroicrobot.dropbit.registry.DeviceRegistry;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javaslang.collection.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pixl.api.delivery.Device;
import pixl.api.delivery.HasDevices;
import pixl.api.plugin.Plugin;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Plugin for a pixel-pusher device.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
@Slf4j
public class PixelpusherPlugin implements Plugin, HasDevices {

    private DeviceRegistry deviceRegistry;
    private PixelPusherDevice ppd = new PixelPusherDevice();

    @Value("${delivery.pixelpusher.devicefilter:}")
    private String pixelPusherFilter;

    @Value("${delivery.pixelpusher.enabled:true}")
    private boolean enabled;

    private Set<InetAddress> knownDevices = Sets.newConcurrentHashSet();

    @Override
    public Collection<Device> getDevices() {
        if (!enabled) {
            return Collections.EMPTY_LIST;
        }
        return ImmutableList.of((Device) ppd);
    }

    @PostConstruct
    public void postConstruct() {
        if (enabled) {
            deviceRegistry = new DeviceRegistry();
            deviceRegistry.startPushing();
            deviceRegistry.setAutoThrottle(true);
            deviceRegistry.setAntiLog(true);

            log.info("Enabling pixelpusher output device");
            if (StringUtils.hasText(pixelPusherFilter)) {
                log.info("Using filter {}", pixelPusherFilter);
            }

            deviceRegistry.addObserver((o, arg) -> {
                if (!(arg instanceof PixelPusher)) {
                    return;
                }

                PixelPusher pixelPusher = (PixelPusher) arg;
                if (!knownDevices.add(pixelPusher.getIp())) {
                    return;
                }

                if (matches(pixelPusher)) {
                    log.info("Discovered and using new device " + pixelPusher);
                } else {
                    log.info("Discovered but not using new device " + pixelPusher);
                }
            });
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (deviceRegistry != null) {
            deviceRegistry.stopPushing();
        }

    }

    class PixelPusherDevice implements Device {

        public void pushImage(Image image) {

            List<PixelPusher> pushers = deviceRegistry.getPushers();
            List<PixelPusher> pixelPushers = Stream.ofAll(pushers)
                    .filter(pixelPusher -> matches(pixelPusher)).toJavaList();

            for (PixelPusher pixelPusher : pixelPushers) {
                List<Strip> strips = pixelPusher.getStrips();
                BufferedImage bufferedImage = (BufferedImage) image;
                for (int y = 0; y < Math.min(strips.size(), image.getHeight(null)); y++) {
                    Strip strip = strips.get(y);
                    for (int x = 0; x < Math.min(strip.getLength(), image.getWidth(null)); x++) {
                        Color color = new Color(bufferedImage.getRGB(x, y));
                        int min = Math.min(Math.min(color.getRed(), color.getBlue()), color.getGreen());
                        int max = Math.max(Math.max(color.getRed(), color.getBlue()), color.getGreen());
                        int avg = (color.getRed() + color.getBlue() + color.getGreen()) / 3;

                        if (Math.abs(min - max) < 10 && Math.abs(avg - min) < 10 && min > 220) {
                            color = new Color((int) (color.getRed() * 0.8), (int) (color.getGreen() * 0.8),
                                    (int) (color.getBlue() * 0.8));

                        }

                        if (Math.abs(min - max) < 10 && Math.abs(avg - min) < 10 && min > 190 && min < 220) {
                            color = new Color((int) (color.getRed() * 0.85), (int) (color.getGreen() * 0.85),
                                    (int) (color.getBlue() * 0.85));

                        }

                        strip.setPixel(color.getRGB(), x);
                    }
                }
            }
        }

        @Override
        public void pushSound(AudioInputStream audioInputStream) {

        }

        @Override
        public rx.Observable<Boolean> triggerButton() {
            return null;
        }

    }

    private boolean matches(PixelPusher pixelPusher) {
        return StringUtils.hasText(pixelPusherFilter) ? pixelPusherFilter
                .contains(pixelPusher.getMacAddress()) || pixelPusherFilter
                .contains(pixelPusher.getIp().toString()) : true;
    }

}
