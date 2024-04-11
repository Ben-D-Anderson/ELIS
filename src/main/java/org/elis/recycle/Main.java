package org.elis.recycle;

import org.elis.recycle.camera.BarcodeScanner;
import org.elis.recycle.camera.CameraException;
import org.elis.recycle.camera.impl.ZXingBarcodeDecoder;
import org.elis.recycle.camera.impl.WebcamCameraViewer;

import java.time.Duration;

public class Main {
    public static void main(String[] args) throws CameraException {
        BarcodeScanner barcodeScanner = new
                BarcodeScanner(WebcamCameraViewer.getDefaultViewer(), new ZXingBarcodeDecoder());

        barcodeScanner.scanBarcode(Duration.ofSeconds(10),
                bufferedImage -> System.out.println(1),
                value -> System.out.println(value));
    }
}