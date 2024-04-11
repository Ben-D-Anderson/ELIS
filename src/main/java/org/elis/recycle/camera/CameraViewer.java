package org.elis.recycle.camera;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface CameraViewer extends AutoCloseable {

    boolean open();

    Optional<BufferedImage> captureImage();

    @Override
    void close();
}
