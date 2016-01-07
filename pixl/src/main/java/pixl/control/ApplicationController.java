package pixl.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import pixl.application.Configuration;
import pixl.fonts.BitmapFont;
import pixl.fonts.BitmapFontParser;
import pixl.fonts.DefaultFont;
import pixl.output.OutputMultiplexer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
@Slf4j
public class ApplicationController {

    @Inject
    private ApplicationRegistry applicationRegistry;

    @Inject
    private PlaylistRegistry playlistRegistry;

    @Inject
    private Configuration configuration;

    @Inject
    private OutputMultiplexer outputMultiplexer;

    private ApplicationActorJob job;
    private ApplicationValueRefresher applicationValueRefresher;
    private Thread thread;
    private ScheduledFuture<?> scheduledFuture;

    @Inject
    private TaskScheduler scheduler;

    @Inject
    private ThreadPoolTaskExecutor executor;

    @PostConstruct
    public void postConstruct() throws Exception {

        BitmapFontParser parser = new BitmapFontParser(new DefaultFont(), DefaultFont.RESOURCE_PREFIX);
        BitmapFont font = parser.parse();

        job = new ApplicationActorJob(playlistRegistry.getPlaylist(), applicationRegistry, configuration, font,
                outputMultiplexer);

        applicationValueRefresher = new ApplicationValueRefresher(executor, playlistRegistry.getPlaylist(),
                applicationRegistry);

        scheduledFuture = scheduler.scheduleAtFixedRate(applicationValueRefresher, TimeUnit.SECONDS.toMillis(60));

        int sleep = 1000 / configuration.getFramerate();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        job.act();
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                    }
                }
            }
        }, getClass().getSimpleName());

        thread.start();
    }

    @PreDestroy
    public void preDestroy() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (scheduledFuture != null) {
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(false);
            }
            scheduledFuture = null;
        }
    }


}
