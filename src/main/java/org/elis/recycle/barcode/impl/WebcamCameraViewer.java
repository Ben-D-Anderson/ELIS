package org.elis.recycle.barcode.impl;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.barcode.CameraViewer;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class WebcamCameraViewer implements CameraViewer {

    private final Webcam webcam;

    static {
        Webcam.setDriver(new V4l4jDriver()); // this is important
    }

    public static WebcamCameraViewer getDefaultViewer() throws CameraException {
        Webcam camera;
        try {
            camera = Webcam.getDefault();
            if (camera == null) {
                throw new CameraException("No camera detected");
            }
        } catch (WebcamException e) {
            e.printStackTrace();
            throw new CameraException(e);
        }
        return new WebcamCameraViewer(camera);
    }

    public WebcamCameraViewer(Webcam webcam) {
        this.webcam = webcam;
    }

    @Override
    public boolean open() {
        try {
            return webcam.open(false);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<BufferedImage> captureImage() {
        return Optional.ofNullable(webcam.getImage());
    }

    @Override
    public void close() {
        webcam.close();
    }

}
