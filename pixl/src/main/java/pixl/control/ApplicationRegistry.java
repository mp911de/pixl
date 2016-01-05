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
import pixl.api.application.HasApplications;
import pixl.api.plugin.Plugin;
import pixl.application.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Service
@Slf4j
public class ApplicationRegistry extends ConfigurationSupport implements ApplicationContextAware {

    public static final String KEY_ACTIVE = "active";
    public static final String KEY_FRAMES = "frames";
    public static final String KEY_ICON = "icon";
    public static final String KEY_FRAME_TYPE = "type";
    public static final String KEY_PREFIX = "prefix";
    public static final String KEY_SUFFIX = "suffix";
    public static final String KEY_DATATYPE = "datatype";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_FORMAT_PATTERN = "formatPattern";
    public static final String KEY_GET_VALUE_ON_REPAINT = "getValueOnRepaint";

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

                readApplicationData((Map<String, Object>) object, applicationFrames);

                this.applicationFrames.put(appId, applicationFrames);
            });
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot parse resource " + resource, e);
        }
    }

    private void readApplicationData(Map<String, Object> applicationData, ApplicationFrames applicationFrames) {

        if (applicationData.containsKey(KEY_ACTIVE)) {
            applicationFrames.setActive((Boolean) applicationData.get(KEY_ACTIVE));
        }

        if (applicationData.containsKey(KEY_FRAMES)) {
            List<Map<String, Object>> framesData = (List<Map<String, Object>>) applicationData.get(KEY_FRAMES);
            List<Frame> frames = applicationFrames.getFrames();

            for (int i = 0; i < framesData.size(); i++) {
                Map<String, Object> frameData = framesData.get(i);
                Frame frame;
                if (frames.size() > i) {
                    frame = frames.get(i);
                } else {
                    frame = new Frame();
                    frames.add(frame);
                }

                readFrameData(frameData, frame);
            }
        }

    }

    private void readFrameData(Map<String, Object> frameData, Frame frame) {
        if (frameData.containsKey(KEY_ICON)) {
            frame.setIcon((String) frameData.get(KEY_ICON));
        }

        if (frameData.containsKey(KEY_FRAME_TYPE)) {
            frame.setFrameType(FrameType.valueOf(((String) frameData.get(KEY_FRAME_TYPE)).toUpperCase()));
        }

        if (frameData.containsKey(KEY_DATATYPE)) {
            frame.setDatatype(Datatype.valueOf(((String) frameData.get(KEY_DATATYPE)).toUpperCase()));
        }

        if (frameData.containsKey(KEY_PREFIX)) {
            frame.setPrefix(toLocalized(frame.getPrefix(), frameData.get(KEY_PREFIX)));
        }

        if (frameData.containsKey(KEY_SUFFIX)) {
            frame.setSuffix(toLocalized(frame.getSuffix(), frameData.get(KEY_SUFFIX)));
        }

        if (frameData.containsKey(KEY_ACTIVE)) {
            frame.setActive((Boolean) frameData.get(KEY_ACTIVE));
        }

        if (frameData.containsKey(KEY_GET_VALUE_ON_REPAINT)) {
            frame.setGetValueOnRepaint((Boolean) frameData.get(KEY_GET_VALUE_ON_REPAINT));
        }

        if (frameData.containsKey(KEY_DURATION)) {
            frame.setDuration(Duration.parse((String) frameData.get(KEY_DURATION)));
        }

        if (frameData.containsKey(KEY_FORMAT_PATTERN)) {
            frame.setFormatPattern(toLocalized(frame.getFormatPattern(), frameData.get(KEY_FORMAT_PATTERN)));
        }
    }

    private Localized toLocalized(Localized localized, Object o) {

        if(localized == null) {
            localized = new Localized();
        }

        if(o instanceof String) {
            localized.setDefaultText((String) o);
        }

        if(o instanceof Map) {
            Map<String, String> map = (Map) o;
            localized.getText().putAll(map);
        }

        return localized;
    }

    public ApplicationFrames getApplicationFrames(String applicationId) {
        return applicationFrames.get(applicationId);
    }

    public Application getApplication(String applicationId) {
        return applications.get(applicationId);
    }
}
