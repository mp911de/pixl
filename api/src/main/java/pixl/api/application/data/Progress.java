package pixl.api.application.data;

import lombok.Getter;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Getter
public class Progress<T extends Number> extends Value<T> {
    public final T min;
    public final T max;

    public Progress(T value, T min, T max) {
        super(value);
        this.min = min;
        this.max = max;
    }

    public static <T extends Number> Progress<T> of(T value, T min, T max) {
        return new Progress<>(value, min, max);
    }
}
