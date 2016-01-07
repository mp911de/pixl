package pixl.control;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import pixl.api.application.Application;
import pixl.api.application.DisplayOpinion;
import pixl.api.application.data.Value;
import pixl.api.delivery.Device;
import pixl.api.userinterface.Frame;
import pixl.application.Configuration;
import pixl.application.Playlist;
import pixl.application.PlaylistItem;
import pixl.fonts.BitmapFont;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class ApplicationActorJob {

    private final Playlist playlist;
    private final ApplicationRegistry applicationRegistry;
    private final Configuration configuration;
    private final BitmapFont bitmapFont;
    private final Device device;

    private final ValueRenderer valueRenderer;
    private final FrameSelector frameSelector;

    private LoadingCache<FormatterKey, Function<Value<?>, String>> formatterCache = CacheBuilder.newBuilder().build(
            new CacheLoader<FormatterKey, Function<Value<?>, String>>() {
                @Override
                public Function<Value<?>, String> load(FormatterKey key) throws Exception {
                    return createFormatter(key);
                }
            });

    private BiFunction<Value<?>, String, String> formatFunction = (value, pattern) -> {
        if (StringUtils.isBlank(pattern)) {
            return value.getValue().toString();
        }

        Function<Value<?>, String> formatter = formatterCache
                .getUnchecked(new FormatterKey(value.getValue().getClass(), pattern));

        return formatter.apply(value);
    };

    private volatile boolean refresh;
    private volatile boolean frameChangeRequested;

    private long playlistItemSince;
    private long frameSince;
    private long transitionSince;

    private FrameController frameController;
    private FrameController lastFrameController;

    private Dimension surfaceDimension = new Dimension(32, 8);
    private Image surface;

    private Clock clock = Clock.systemDefaultZone();

    public ApplicationActorJob(Playlist playlist, ApplicationRegistry applicationRegistry,
                               Configuration configuration, BitmapFont bitmapFont, Device device) {
        this.playlist = playlist;
        this.applicationRegistry = applicationRegistry;
        this.configuration = configuration;
        this.bitmapFont = bitmapFont;
        this.device = device;

        valueRenderer = new ValueRenderer(bitmapFont);
        this.frameSelector = new FrameSelector(playlist, applicationRegistry);

        device.triggerButton().subscribe(state -> {
            frameChangeRequested = true;
        });
    }

    public void act() {
        if (frameController == null) {
            refresh = true;
        }

        if (surface == null) {
            surface = new BufferedImage(surfaceDimension.width, surfaceDimension.height, BufferedImage.TYPE_INT_RGB);
            playlistItemSince = now();
        }

        int surfaceHeight = surface.getHeight(null);
        int surfaceWidth = surface.getWidth(null);

        boolean switchFrame = false;

        if (playlistItemSince != 0 && frameSince != 0 && transitionSince == 0 && !frameController
                .isInFrameAnimation()) {
            long applicationDuration = now() - playlistItemSince;
            long frameDuration = now() - frameSince;
            switchFrame = frameSelector.frameSwitchNeeded(applicationDuration, frameDuration);
        }

        if (frameChangeRequested) {
            frameChangeRequested = false;
            switchFrame = true;
        }

        if (switchFrame) {
            FrameSelector.FrameSelection frameSelection = frameSelector.nextFrame();

            lastFrameController = frameController;
            frameController = null;
            transitionSince = now();

            if (frameSelection.playlistItemChanged) {
                playlistItemSince = now();
            }
        }

        PlaylistItem currentPlaylistItem = frameSelector.getPlaylistItem();
        Application application = applicationRegistry.getApplication(currentPlaylistItem.getApplication());
        Frame frame = frameSelector.getFrame();

        if (frameController == null || switchFrame) {
            frameController = new FrameController(frameSelector.getFrameIndex(), frame, application,
                    formatFunction, valueRenderer);
            frameSince = now();
        }

        if (switchFrame || refresh || frame.isGetValueOnRepaint()) {
            frameController
                    .createImage(refresh, surfaceHeight, surfaceWidth, LocaleUtils.toLocale(configuration.getLocale()),
                            currentPlaylistItem.getConfiguration());
            refresh = false;
        }

        Graphics graphics = surface.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, surfaceWidth, surfaceHeight);

        int textOffsetX = 0;

        if (lastFrameController != null) {
            lastFrameController
                    .createImage(false, surfaceHeight, surfaceWidth, LocaleUtils.toLocale(configuration.getLocale()),
                            currentPlaylistItem.getConfiguration());

            long transitionDisplayedDuration = (now() - transitionSince);
            double scrollDuration = (surfaceHeight / Animation.scrollPixelPerSecond) * 1000;
            long animationPhase = (long) scrollDuration;

            double ms = ((transitionDisplayedDuration / (double) animationPhase)) * (double) animationPhase;
            int scrollOffsetY = (int) (((ms) / scrollDuration) * surfaceHeight);

            if (scrollOffsetY > surfaceHeight) {
                lastFrameController = null;
                transitionSince = 0;
                frameSince = now();
            } else {
                lastFrameController
                        .renderAnimatedImage(graphics, surfaceHeight, surfaceWidth, textOffsetX,
                                scrollOffsetY);
                graphics.translate(0, scrollOffsetY - surfaceHeight);
            }
        }

        long frameDisplayedDuration = (now() - frameSince);
        frameController.setFrameDisplayedDuration(frameDisplayedDuration);
        frameController.renderAnimatedImage(graphics, surfaceHeight, surfaceWidth, textOffsetX,
                0);

        graphics.dispose();

        device.pushImage(surface);
    }

    private Function<Value<?>, String> createFormatter(FormatterKey key) {

        if (Date.class.isAssignableFrom(key.getType())) {
            Locale locale = LocaleUtils.toLocale(configuration.getLocale());

            DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
            SimpleDateFormat format = new SimpleDateFormat(key.getPattern(), dateFormatSymbols);
            return value -> format.format(value.getValue());
        }

        if (Number.class.isAssignableFrom(key.getType())) {
            Locale locale = LocaleUtils.toLocale(configuration.getLocale());

            DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
            DecimalFormat format = new DecimalFormat(key.getPattern(), decimalFormatSymbols);
            return value -> format.format(value.getValue());
        }

        return value -> value.getValue().toString();
    }

    private long now() {
        return clock.millis();
    }

    @lombok.Value
    private class FormatterKey {
        private final Class<?> type;
        private final String pattern;
    }
}
