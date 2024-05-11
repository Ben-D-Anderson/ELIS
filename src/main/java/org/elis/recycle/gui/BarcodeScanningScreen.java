package org.elis.recycle.gui;

import org.elis.recycle.barcode.BarcodeScanner;
import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.barcode.impl.WebcamCameraViewer;
import org.elis.recycle.barcode.impl.ZXingBarcodeDecoder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BarcodeScanningScreen extends JPanel {

    private final AtomicBoolean scanning = new AtomicBoolean(false);
    private final Object imageLock = new Object();
    private Image image;

    private final JLabel loadingGifLabel = new JLabel();
    private final AtomicBoolean loadingGifAdded = new AtomicBoolean(false);

    //todo: add back button to go back to main screen
    public BarcodeScanningScreen() {
        super();
        setLayout(new GridBagLayout());

        try {
            URL loadingGifURL = Objects.requireNonNull(getClass().getResource("/loading.gif"));
            loadingGifLabel.setIcon(new ImageIcon(loadingGifURL));
            loadingGifLabel.setOpaque(false);
        } catch (NullPointerException e) {
            loadingGifLabel.setText("No Image Available");
        }
        addLoadingGif();
    }

    private void addLoadingGif() {
        if (!loadingGifAdded.get()) {
            add(loadingGifLabel);
            loadingGifAdded.set(true);
        }
    }

    private void removeLoadingGif() {
        if (loadingGifAdded.get()) {
            remove(loadingGifLabel);
            loadingGifAdded.set(false);
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
            removeLoadingGif();
            repaint();
        });
    }

    private void onFinish() {
        scanning.set(false);
        synchronized (imageLock) {
            this.image = null;
        }
        try {
            SwingUtilities.invokeAndWait(() -> {
                addLoadingGif();
                repaint();
            });
        } catch (InterruptedException | InvocationTargetException ignored) {}
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!loadingGifAdded.get()) {
            synchronized (imageLock) {
                if (image != null) {
                    g.drawImage(image, getWidth(), 0, -getWidth(), getHeight(), null);
                }
            }
        }
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
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
