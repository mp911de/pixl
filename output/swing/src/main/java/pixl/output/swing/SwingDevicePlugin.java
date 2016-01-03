package pixl.output.swing;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pixl.api.delivery.Device;
import pixl.api.delivery.HasDevices;
import pixl.api.plugin.Plugin;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Slf4j
@Component
public class SwingDevicePlugin implements Plugin, HasDevices {

    @Value("${output.swing.enabled:true}")
    private boolean enabled;

    private SwingDesktopDevice swingDesktopDevice = new SwingDesktopDevice();

    @PostConstruct
    public void postConstruct() {
        if (enabled) {
            log.info("Enabling swing output device");
        }
    }

    @Override
    public Collection<Device> getDevices() {
        if (enabled) {
            return ImmutableList.of((Device) swingDesktopDevice);
        }
        return Collections.EMPTY_LIST;
    }
}
