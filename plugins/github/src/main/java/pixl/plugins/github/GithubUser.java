package pixl.plugins.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubUser {
    private String login;
}
