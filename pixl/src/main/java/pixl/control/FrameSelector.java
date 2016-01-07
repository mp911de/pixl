package pixl.control;

import pixl.api.application.Application;
import pixl.api.application.DisplayOpinion;
import pixl.api.userinterface.ApplicationFrames;
import pixl.api.userinterface.Frame;
import pixl.application.Playlist;
import pixl.application.PlaylistItem;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class FrameSelector {

    private final Playlist playlist;
    private final ApplicationRegistry applicationRegistry;
    private int frameIndex;
    private int playlistIndex;

    public FrameSelector(Playlist playlist, ApplicationRegistry applicationRegistry) {
        this.playlist = playlist;
        this.applicationRegistry = applicationRegistry;
    }

    public boolean frameSwitchNeeded(long applicationDuration, long frameDuration) {

        PlaylistItem playlistItem = playlist.getItem(playlistIndex);
        ApplicationFrames applicationFrames = applicationRegistry
                .getApplicationFrames(playlistItem.getApplication());

        if (playlistItem.getDuration() != null) {
            if (playlistItem.getDuration().toMillis() < applicationDuration) {
                return true;
            }
        }

        Frame frame = applicationFrames.getFrame(frameIndex);
        if (frame.getDuration() != null) {
            if (frame.getDuration().toMillis() < frameDuration) {
                return true;
            }
        }

        return false;
    }

    public String getNextApplicationId() {

        int nextPlaylistIndex = playlistIndex + 1;

        if (playlistIndex >= playlist.size()) {
            nextPlaylistIndex = 0;
        }

        String application = playlist.getItem(nextPlaylistIndex).getApplication();
        return application;
    }

    public FrameSelection nextFrame() {

        boolean switchPlaylistItem = false;

        PlaylistItem verifyPlaylistItem = playlist.getItem(playlistIndex);
        ApplicationFrames verifyApplicationFrames = applicationRegistry
                .getApplicationFrames(verifyPlaylistItem.getApplication());

        int localFrameIndex = frameIndex;
        int localPlaylistIndex = playlistIndex;

        for (int i = 0; i < 5; i++) {
            localFrameIndex++;
            if (localFrameIndex >= verifyApplicationFrames.getFrameCount()) {
                localFrameIndex = 0;
                switchPlaylistItem = true;
            }

            if (switchPlaylistItem) {
                localPlaylistIndex++;

                if (localPlaylistIndex >= playlist.size()) {
                    localPlaylistIndex = 0;
                    localFrameIndex = 0;
                }
            }

            PlaylistItem playlistItem = playlist.getItem(localPlaylistIndex);
            Application application = applicationRegistry.getApplication(playlistItem.getApplication());

            if (application instanceof DisplayOpinion) {
                if (!((DisplayOpinion) application).shouldDisplay(playlistItem.getConfiguration())) {
                    continue;
                }
            }
            break;
        }

        frameIndex = localFrameIndex;
        playlistIndex = localPlaylistIndex;

        ApplicationFrames applicationFrames = applicationRegistry
                .getApplicationFrames(verifyPlaylistItem.getApplication());

        return new FrameSelection(verifyPlaylistItem, applicationFrames.getFrame(frameIndex), switchPlaylistItem);
    }

    public PlaylistItem getPlaylistItem() {
        return playlist.getItem(playlistIndex);
    }

    public Frame getFrame() {
        return applicationRegistry.getApplicationFrames(getPlaylistItem().getApplication()).getFrame(frameIndex);
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public static class FrameSelection {
        final PlaylistItem playlistItem;
        final Frame frame;
        final boolean playlistItemChanged;

        public FrameSelection(PlaylistItem playlistItem, Frame frame, boolean playlistItemChanged) {
            this.playlistItem = playlistItem;
            this.frame = frame;
            this.playlistItemChanged = playlistItemChanged;
        }
    }

}
