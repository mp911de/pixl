package pixl.api.delivery;

import java.util.Collection;

/**
 * Plugin that provides devices.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface HasDevices {
    Collection<Device> getDevices();
}
