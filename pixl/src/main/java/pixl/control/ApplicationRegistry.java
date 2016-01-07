package pixl.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import javaslang.collection.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pixl.api.application.Application;
import pixl.api.application.ApplicationRegistrySupport;
import pixl.api.application.HasApplications;
import pixl.api.plugin.Plugin;
import pixl.api.userinterface.ApplicationFrames;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Service
@Slf4j
public class ApplicationRegistry extends ConfigurationSupport implements ApplicationContextAware {

    private Map<String, ApplicationFrames> applicationFrames = Maps.newHashMap();
    private Map<String, Application> applications = Maps.newHashMap();
    private ApplicationContext applicationContext;

    @PostConstruct
    protected void postConstruct() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        parseResources(resourcePatternResolver.getResources("classpath*:application.json"), objectMapper);
        parseResources(resourcePatternResolver.getResources("classpath*:**/application.json"), objectMapper);

        File config = new File("config");
        if (config.exists()) {
            parseResources(fileResourcePatternResolver.getResources("config/**/application.json"), objectMapper);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("Loading application plugins");
        this.applicationContext = applicationContext;
        applications.clear();

        Map<String, Plugin> beans = applicationContext.getBeansOfType(Plugin.class);

        Stream.ofAll(beans.values()).filter(plugin -> plugin instanceof HasApplications)
                .map(plugin -> (HasApplications) plugin)
                .forEach(
                        plugin ->
                        {
                            Collection<Application> applications = plugin.getApplications();
                            log.info("Using {} applications(s) from Plugin {}", applications.size(), plugin);

                            for (Application application : applications) {
                                this.applications.put(application.getId(), application);
                            }
                        }
                );
    }

    private void parseResources(Resource[] resources, ObjectMapper objectMapper) throws IOException {
        for (Resource resource : resources) {
            if (!resource.isReadable() || !resource.exists()) {
                continue;
            }

            log.debug("Loading applications from {}", resource);

            readApplicationJson(objectMapper, resource);
        }
    }

    private void readApplicationJson(ObjectMapper objectMapper, Resource resource) throws IOException {
        try {
            Map<String, Object> map = objectMapper.readValue(resource.getURL(), Map.class);
            map.forEach((appId, object) -> {
                ApplicationFrames applicationFrames = this.applicationFrames
                        .computeIfAbsent(appId, key -> new ApplicationFrames());
                applicationFrames.setId(appId);

                ApplicationRegistrySupport.readApplicationData((Map<String, Object>) object, applicationFrames);

                this.applicationFrames.put(appId, applicationFrames);
            });
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot parse resource " + resource, e);
        }
    }

    public ApplicationFrames getApplicationFrames(String applicationId) {
        return applicationFrames.get(applicationId);
    }

    public Application getApplication(String applicationId) {
        return applications.get(applicationId);
    }
}
