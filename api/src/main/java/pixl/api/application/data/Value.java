package pixl.api.application.data;

import lombok.Getter;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Getter
public class Value<T> {
    public final T value;

    public Value(T value) {
        this.value = value;
    }

    public static <T> Value<T> of(T value) {
        return new Value<>(value);
    }
}
