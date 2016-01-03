package pixl.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pixl.api.application.Application;
import pixl.application.ApplicationFrames;
import pixl.application.Playlist;
import pixl.application.PlaylistItem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Service
public class PlaylistRegistry extends ConfigurationSupport {

    public static final String PLAYLIST_FILE = "playlist.json";

    @Inject
    private ApplicationRegistry applicationRegistry;

    private Playlist playlist;

    @PostConstruct
    protected void postConstruct() throws IOException {
        File config = new File("config");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (config.exists()) {
            Resource[] resources = fileResourcePatternResolver.getResources("config/" + PLAYLIST_FILE);
            if (resources.length == 1) {
                playlist = objectMapper.readValue(resources[0].getURL(), Playlist.class);
            }
        } else {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:" + PLAYLIST_FILE);
            if (resources.length == 1) {
                playlist = objectMapper.readValue(resources[0].getURL(), Playlist.class);
            }
        }

        if (playlist == null) {
            throw new IllegalStateException(
                    "Playlist file " + PLAYLIST_FILE + " not found (config directory, class-path)");
        }

        for (PlaylistItem playlistItem : playlist.getItems()) {
            ApplicationFrames applicationFrames = applicationRegistry.getApplicationFrames(
                    playlistItem.getApplication());
            if (applicationFrames == null) {
                throw new IllegalStateException(
                        "Invalid application reference. No frames for application " + playlistItem.getApplication());
            }

            Application application = applicationRegistry.getApplication(playlistItem.getApplication());
            if (application == null) {
                throw new IllegalStateException(
                        "Invalid application reference. Application " + playlistItem.getApplication() + " not found");
            }
        }
    }

    public Playlist getPlaylist() {
        return playlist;
    }
}
