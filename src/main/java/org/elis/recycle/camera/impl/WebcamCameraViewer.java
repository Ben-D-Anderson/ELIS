package org.elis.recycle.camera.impl;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import org.elis.recycle.camera.CameraException;
import org.elis.recycle.camera.CameraViewer;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class WebcamCameraViewer implements CameraViewer {

    private final Webcam webcam;

    public static WebcamCameraViewer getDefaultViewer() throws CameraException {
        Webcam camera;
        try {
            camera = Webcam.getDefault();
            if (camera == null) {
                throw new CameraException("No camera detected");
            }
        } catch (WebcamException e) {
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
            return webcam.open();
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
