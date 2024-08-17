package org.elis.recycle.barcode.impl;

import org.elis.recycle.barcode.CameraException;
import org.elis.recycle.barcode.CameraViewer;
import uk.co.caprica.picam.*;
import uk.co.caprica.picam.enums.Encoding;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static uk.co.caprica.picam.CameraConfiguration.cameraConfiguration;
import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

public class PiCamViewer implements CameraViewer {

    private final Camera camera;
    private final PictureCaptureHandler<byte[]> byteCaptureHandler;
    private final PictureCaptureHandler<File> fileCaptureHandler;
    private final Path tempImageFile;
    private boolean prioritiseFiles = false;

    public PiCamViewer() throws CameraException {
        try {
            installTempLibrary();
            CameraConfiguration cameraConfiguration = cameraConfiguration()
                    .width(1024)
                    .height(600)
                    .encoding(Encoding.JPEG);
            camera = new Camera(cameraConfiguration);
            byteCaptureHandler = new ByteArrayPictureCaptureHandler();
            tempImageFile = Paths.get(System.getProperty("java.io.tmpdir"), "temp_picam_image.jpeg");
            fileCaptureHandler = new FilePictureCaptureHandler(tempImageFile);
        } catch (NativeLibraryException | uk.co.caprica.picam.CameraException e) {
            throw new CameraException(e);
        }
    }

    @Override
    public boolean open() {
        return camera.open();
    }

    @Override
    public Optional<BufferedImage> captureImage() {
        try {
            BufferedImage image = null;
            if (!prioritiseFiles) {
                byte[] imageData = camera.takePicture(byteCaptureHandler);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
                image = ImageIO.read(byteArrayInputStream);
                if (image == null) {
                    prioritiseFiles = true;
                }
            }
            if (prioritiseFiles) {
                camera.takePicture(fileCaptureHandler);
                image = ImageIO.read(tempImageFile.toFile());
            }
            return Optional.ofNullable(image);
        } catch (IOException | CaptureFailedException e) {
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        camera.close();
    }

}
