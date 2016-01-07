package pixl.defaultapplications;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pixl.api.application.Application;
import pixl.api.application.HasApplications;
import pixl.api.plugin.Plugin;
import pixl.defaultapplications.time.MinuteProgressApplication;
import pixl.defaultapplications.time.TimeApplication;
import pixl.defaultapplications.weather.OpenWeatherMapApplication;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class DefaultApplications implements Plugin, HasApplications {

    @Inject
    private OpenWeatherMapApplication weather;

    @Override
    public Collection<Application> getApplications() {
        List<Application> applications = Lists.newArrayList();

        applications.add(new TimeApplication());
        applications.add(new MinuteProgressApplication());
        applications.add(weather);
        applications.add(new TextApplication());
        applications.add(new Text2Application());
        return applications;
    }
}
