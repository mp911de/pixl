package pixl.api.application.data;

import lombok.Getter;

import java.awt.*;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Getter
public class IconValue<T> extends Value<T> {
    public final Image icon;

    public IconValue(T value, Image icon) {
        super(value);
        this.icon = icon;
    }

    public static <T> IconValue<T> of(T value, Image icon) {
        return new IconValue<>(value, icon);
    }
}
