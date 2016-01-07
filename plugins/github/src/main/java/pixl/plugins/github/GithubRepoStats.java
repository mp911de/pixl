package pixl.plugins.github;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Data
public class GithubRepoStats {

    private List<GithubIssue> issues;
    private List<GithubIssue> pulls;
}
