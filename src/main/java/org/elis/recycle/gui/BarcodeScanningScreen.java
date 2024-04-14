package org.elis.recycle.gui;

import org.elis.recycle.barcode.BarcodeScanner;
import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.barcode.impl.WebcamCameraViewer;
import org.elis.recycle.barcode.impl.ZXingBarcodeDecoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BarcodeScanningScreen extends JPanel {

    private final AtomicBoolean scanning = new AtomicBoolean(false);
    private final Object imageLock = new Object();
    private Image image;

    private final JLabel loadingImageLabel = new JLabel();
    private final AtomicBoolean noImageLabelAdded = new AtomicBoolean(false);

    public BarcodeScanningScreen() {
        super();
        setLayout(new GridBagLayout());

        try {
            URL loadingGifURL = Objects.requireNonNull(getClass().getResource("/loading.gif"));
            loadingImageLabel.setIcon(new ImageIcon(loadingGifURL));
        } catch (NullPointerException e) {
            loadingImageLabel.setText("No Image Available");
        }
        addNoImageText();
    }

    private void addNoImageText() {
        if (!noImageLabelAdded.get()) {
            add(loadingImageLabel);
            noImageLabelAdded.set(true);
        }
    }

    private void removeNoImageText() {
        if (noImageLabelAdded.get()) {
            remove(loadingImageLabel);
            noImageLabelAdded.set(false);
        }
    }

    public void updateImage(BufferedImage image) {
        if (!scanning.get()) return;

        synchronized (imageLock) {
            this.image = image.getScaledInstance(
                            getWidth(),
                            getHeight(),
                            BufferedImage.SCALE_FAST);
        }
        SwingUtilities.invokeLater(() -> {
            removeNoImageText();
            repaint();
        });
    }

    private void onFinish() {
        scanning.set(false);
        synchronized (imageLock) {
            this.image = null;
        }
        SwingUtilities.invokeLater(() -> {
            addNoImageText();
            repaint();
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        synchronized (imageLock) {
            if (image != null) {
                g.drawImage(image, getWidth(), 0, -getWidth(), getHeight(), null);
            }
        }
    }

    /**
     * @param timeout until the barcode scanning expires automatically if no barcode was found,
     *                timeoutCallback will then be called.
     * @param barcodeCallback ran with the read barcode when found - this is run on a worker thread,
     *                        if you would like to use this to access GUI code ensure to wrap it in
     *                        {@code SwingUtilities.invoke}.
     * @param timeoutCallback ran when the operation times out - this is run on a worker thread,
     *                        if you would like to use this to access GUI code ensure to wrap it in
     *                        {@code SwingUtilities.invoke}.
     */
    public void start(Duration timeout, Consumer<String> barcodeCallback, Runnable timeoutCallback)
            throws CameraException {
        BarcodeScanner barcodeScanner = new BarcodeScanner(
                WebcamCameraViewer.getDefaultViewer(),
                new ZXingBarcodeDecoder()
        );

        scanning.set(true);
        barcodeScanner.scanBarcode(
                timeout,
                this::updateImage,
                barcodeStr -> {
                    onFinish();
                    barcodeCallback.accept(barcodeStr);
                },
                () -> {
                    onFinish();
                    timeoutCallback.run();
                }
        );
    }

}
