package pixl.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import pixl.api.application.Application;
import pixl.application.Playlist;
import pixl.application.PlaylistItem;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Slf4j
public class ApplicationValueRefresher implements Runnable {
    private final TaskExecutor taskExecutor;
    private final Playlist playlist;
    private final ApplicationRegistry applicationRegistry;

    public ApplicationValueRefresher(TaskExecutor taskExecutor, Playlist playlist,
                                     ApplicationRegistry applicationRegistry) {
        this.taskExecutor = taskExecutor;
        this.playlist = playlist;
        this.applicationRegistry = applicationRegistry;
    }

    @Override
    public void run() {

        for (PlaylistItem playlistItem : playlist.getItems()) {
            Application application = applicationRegistry.getApplication(playlistItem.getApplication());

            taskExecutor.execute(() -> {
                try {
                    application.getValues(playlistItem.getConfiguration());
                } catch (Exception e) {
                    log.warn("Cannot retrieve values for application " + application.getId(), e);
                }
            });

        }
    }
}
