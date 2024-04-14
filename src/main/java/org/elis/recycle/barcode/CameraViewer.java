package org.elis.recycle.barcode;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface CameraViewer extends AutoCloseable {

    boolean open();

    Optional<BufferedImage> captureImage();

    @Override
    void close();
}
