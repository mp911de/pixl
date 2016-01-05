package pixl.output.swing;

import pixl.api.delivery.Device;
import pixl.api.delivery.Startable;
import pixl.api.delivery.Stoppable;
import com.sun.org.apache.xpath.internal.operations.Bool;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;

/**
 * Desktop output device.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class SwingDesktopDevice implements Device, Startable, Stoppable {

    private JFrame frame;
    private SwingDisplay swingDisplay = new SwingDisplay();
    private Subject<Boolean, Boolean> subject = PublishSubject.create();

    @Override
    public void pushImage(Image image) {
        swingDisplay.setImage(image);
    }

    @Override
    public void pushSound(AudioInputStream audioInputStream) {

    }

    @Override
    public Observable<Boolean> triggerButton() {
        return subject;
    }

    @Override
    public void start() {

        swingDisplay.initialize();

        frame = new JFrame("Pixl " + getClass().getSimpleName());

        frame.setMinimumSize(new Dimension(400, 400));
        frame.setSize(new Dimension(400, 400));
        frame.setPreferredSize(new Dimension(400, 400));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (int) ((screenSize.getWidth() / 2) - (frame.getSize().getWidth() / 2));
        int y = (int) ((screenSize.getHeight() / 2) - (frame.getSize().getHeight() / 2));

        swingDisplay.getBtnTrigger().addActionListener(e -> subject.onNext(true));

        frame.setLocation(x, y);
        frame.add(swingDisplay.getPanel());

        frame.setVisible(true);

    }

    @Override
    public void stop() {
        frame.setVisible(false);
    }
}
