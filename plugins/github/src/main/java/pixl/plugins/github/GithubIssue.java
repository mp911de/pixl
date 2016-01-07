package pixl.plugins.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubIssue {

    private String title;
    private GithubUser user;
    private GithubUser assignee;
    private Object pull_request;
}
