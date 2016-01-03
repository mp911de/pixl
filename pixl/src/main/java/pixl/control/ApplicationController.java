package pixl.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pixl.application.Configuration;
import pixl.fonts.BitmapFont;
import pixl.fonts.BitmapFontParser;
import pixl.fonts.DefaultFont;
import pixl.output.OutputMultiplexer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
    private Thread thread;

    @PostConstruct
    public void postConstruct() throws Exception {

        BitmapFontParser parser = new BitmapFontParser(new DefaultFont(), DefaultFont.RESOURCE_PREFIX);
        BitmapFont font = parser.parse();

        job = new ApplicationActorJob(playlistRegistry.getPlaylist(), applicationRegistry, configuration, font,
                outputMultiplexer);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        job.act();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }catch (Exception e) {
                        e.printStackTrace();
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
        }
    }


}
