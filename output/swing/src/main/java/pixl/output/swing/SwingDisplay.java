package pixl.output.swing;

import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class SwingDisplay {
    private JComboBox cboScreenDimension;
    private JButton btnTrigger;
    private JPanel canvas;
    private JPanel panel;
    private JComboBox cboScale;
    private Image image;

    private Resolution resolution;
    private Scale scale;

    // TODO: either generate code here or use gradle plugin to generate code from IDEA design
    private void createUIComponents() {
        canvas = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                BufferedImage image = new BufferedImage(resolution.width, resolution.height,
                        BufferedImage.TYPE_INT_RGB);

                Graphics graphics = image.getGraphics();

                if (SwingDisplay.this.image != null) {
                    graphics.drawImage(SwingDisplay.this.image, 0, 0, null);
                }
                graphics.dispose();

                Dimension size = canvas.getSize();

                int targetWidth = resolution.width * scale.factor;
                int targetHeight = resolution.height * scale.factor;

                int targetX = (size.width / 2) - (targetWidth / 2);
                int targetY = (size.height / 2) - (targetHeight / 2);

                g.drawImage(image, targetX, targetY, targetWidth, targetHeight, null);
            }
        };
    }

    public void initialize() {

        DefaultComboBoxModel<Resolution> dimensions = new DefaultComboBoxModel<Resolution>();

        this.resolution = new Resolution(8, 32);
        dimensions.addElement(resolution);
        dimensions.addElement(new Resolution(16, 32));
        dimensions.addElement(new Resolution(32, 32));
        dimensions.addElement(new Resolution(16, 64));
        dimensions.addElement(new Resolution(32, 64));
        dimensions.setSelectedItem(resolution);

        cboScreenDimension.setModel(dimensions);
        cboScreenDimension.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                resolution = (Resolution) e.getItem();
                panel.repaint();
            }
        });

        DefaultComboBoxModel<Scale> scaling = new DefaultComboBoxModel<Scale>();

        scaling.addElement(new Scale(1));
        scaling.addElement(new Scale(2));

        scale = new Scale(5);
        scaling.addElement(scale);
        scaling.addElement(new Scale(10));
        scaling.setSelectedItem(scale);

        cboScale.setModel(scaling);
        cboScale.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                scale = (Scale) e.getItem();
                panel.repaint();
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setImage(Image image) {
        this.image = image;
        panel.repaint();
    }

    @Data
    private static class Resolution {

        private int height;
        private int width;

        public Resolution(int height, int width) {
            this.height = height;
            this.width = width;
        }

        @Override
        public String toString() {
            return height + "x" + width;
        }
    }

    @Data
    private static class Scale {

        private int factor;

        public Scale(int factor) {
            this.factor = factor;
        }

        @Override
        public String toString() {
            return factor + "x";
        }
    }
}
