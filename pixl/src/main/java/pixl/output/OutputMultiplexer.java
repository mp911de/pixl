package pixl.output;

import com.google.common.collect.Lists;
import javaslang.collection.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pixl.api.delivery.Device;
import pixl.api.delivery.HasDevices;
import pixl.api.delivery.Startable;
import pixl.api.delivery.Stoppable;
import pixl.api.plugin.Plugin;
import rx.Observable;
import rx.Subscriber;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class OutputMultiplexer implements Device, ApplicationContextAware {
    private final static Logger log = LoggerFactory.getLogger(OutputMultiplexer.class);

    private ApplicationContext applicationContext;
    private Collection<Device> devices = Lists.newArrayList();

    @Override
    public void pushImage(Image image) {
        Stream.ofAll(devices).forEach(device -> device.pushImage(image));
    }

    @Override
    public void pushSound(AudioInputStream audioInputStream) {

    }

    @Override
    public Observable<Boolean> triggerButton() {

        Map<String, Plugin> beans = applicationContext.getBeansOfType(Plugin.class);
        Observable.OnSubscribe<Boolean> onSubscribe = new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onStart();
                Stream.ofAll(devices).forEach(device -> {

                            Observable<Boolean> deviceObservable = device.triggerButton();
                            if (deviceObservable != null) {

                                deviceObservable.subscribe(state -> {
                                    subscriber.onNext(state);
                                });
                            }
                        }
                );


            }
        };

        return Observable.create(onSubscribe);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("Loading device plugins");
        this.applicationContext = applicationContext;
        devices.clear();

        Map<String, Plugin> beans = applicationContext.getBeansOfType(Plugin.class);

        Stream.ofAll(beans.values()).filter(plugin -> plugin instanceof HasDevices)
                .map(plugin -> (HasDevices) plugin)
                .forEach(
                        plugin ->
                        {
                            Collection<Device> devices = plugin.getDevices();
                            log.info("Using {} device(s) from Plugin {}", devices.size(), plugin);
                            this.devices.addAll(devices);
                        }
                );
    }

    @PostConstruct
    public void start() {
        log.info("Starting devices");
        Stream.ofAll(devices).filter(device -> device instanceof Startable).map(device -> (Startable) device)
                .forEach(
                        Startable::start);
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping devices");
        Stream.ofAll(devices).filter(device -> device instanceof Stoppable).map(device -> (Stoppable) device)
                .forEach(
                        Stoppable::stop);
    }
}
