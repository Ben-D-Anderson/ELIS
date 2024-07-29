package org.elis.recycle.barcode.impl;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.barcode.CameraViewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class RPiCamViewer implements CameraViewer {

    private final RPiCamera camera;

    public RPiCamViewer() throws CameraException {
        try {
            camera = new RPiCamera();
        } catch (FailedToRunRaspistillException e) {
            throw new CameraException(e);
        }
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public Optional<BufferedImage> captureImage() {
        try {
            return Optional.ofNullable(camera.takeBufferedStill());
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }

    @Override
    public void close() {

    }
}
