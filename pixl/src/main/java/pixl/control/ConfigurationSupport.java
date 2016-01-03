package pixl.control;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public abstract class ConfigurationSupport implements ResourceLoaderAware {

    protected ResourcePatternResolver resourcePatternResolver;
    protected ResourcePatternResolver fileResourcePatternResolver;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.fileResourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(
                new FileSystemResourceLoader());
    }


}
