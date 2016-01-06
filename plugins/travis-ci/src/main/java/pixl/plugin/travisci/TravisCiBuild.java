package pixl.plugin.travisci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TravisCiBuild {
    String state;
    Number result;
}
