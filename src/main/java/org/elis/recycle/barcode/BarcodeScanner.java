package org.elis.recycle.barcode;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BarcodeScanner {

    private static final int MAXIMUM_CAMERA_OPEN_ATTEMPTS = 5;
    private static final long CAMERA_OPEN_REATTEMPT_DELAY_MILLISECONDS = 100;

    private final CameraViewer camera;
    private final BarcodeDecoder decoder;
    private final AtomicBoolean barcodeFound;

    public BarcodeScanner(CameraViewer cameraViewer, BarcodeDecoder barcodeDecoder) {
        this.camera = cameraViewer;
        this.decoder = barcodeDecoder;
        this.barcodeFound = new AtomicBoolean(false);
    }

    public void scanBarcode(Duration timeout,
                            Consumer<BufferedImage> liveImageViewer,
                            Consumer<String> barcodeCallback,
                            Runnable timeoutCallback) throws CameraException {

        int cameraOpenAttempts = 0;
        while (!camera.open() && ++cameraOpenAttempts <= MAXIMUM_CAMERA_OPEN_ATTEMPTS) {
            try {
                Thread.sleep(CAMERA_OPEN_REATTEMPT_DELAY_MILLISECONDS);
            } catch (InterruptedException ignored) {}
        }
        if (cameraOpenAttempts >= MAXIMUM_CAMERA_OPEN_ATTEMPTS)
            throw new CameraException("Failed to open camera after " + MAXIMUM_CAMERA_OPEN_ATTEMPTS + " attempts." +
                    "\nIs it being used by another process?");

        BarcodeImageProcessor imageProcessor = new BarcodeImageProcessor(barcodeCallback);
        imageProcessor.start();

        LocalDateTime timeoutDateTime = LocalDateTime.now().plus(timeout);
        try {
            while (LocalDateTime.now().isBefore(timeoutDateTime)
                    && !barcodeFound.get()) {
                Optional<BufferedImage> capturedImageOptional = camera.captureImage();
                if (capturedImageOptional.isEmpty()) continue;

                imageProcessor.setImageToProcess(capturedImageOptional.get());
                liveImageViewer.accept(capturedImageOptional.get());
            }
        } catch (Exception ignored) {}

        imageProcessor.close();
        camera.close();

        if (!barcodeFound.get()) timeoutCallback.run();
        barcodeFound.set(false);
    }

    private class BarcodeImageProcessor extends Thread implements AutoCloseable {

        private final AtomicBoolean readyForImage;
        private final AtomicBoolean running;
        private final Consumer<String> barcodeCallback;

        private volatile BufferedImage imageToProcess;

        private BarcodeImageProcessor(Consumer<String> barcodeCallback) {
            this.readyForImage = new AtomicBoolean(true);
            this.running = new AtomicBoolean(true);
            this.barcodeCallback = barcodeCallback;
        }

        public void setImageToProcess(BufferedImage bufferedImage) {
            if (readyForImage.get()) {
                imageToProcess = bufferedImage;
                readyForImage.set(false);
            }
        }

        @Override
        public void run() {
            while (running.get()) {
                if (!readyForImage.get()) {
                    Optional<String> decodedBarcodeOptional = decoder.decodeBarcode(imageToProcess);
                    readyForImage.set(true);
                    decodedBarcodeOptional.ifPresent(value -> {
                        barcodeFound.set(true);
                        barcodeCallback.accept(value);
                        close();
                    });
                }
            }
        }

        @Override
        public void close() {
            running.set(false);
        }
    }

}
