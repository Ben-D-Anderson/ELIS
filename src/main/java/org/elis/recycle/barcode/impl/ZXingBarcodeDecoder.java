package org.elis.recycle.barcode.impl;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.elis.recycle.barcode.BarcodeDecoder;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class ZXingBarcodeDecoder implements BarcodeDecoder {

    @Override
    public Optional<String> decodeBarcode(BufferedImage image) {
        BinaryBitmap binaryBitmap = new BinaryBitmap(
                new HybridBinarizer(
                        new BufferedImageLuminanceSource(image)
                )
        );
        try {
            Result result = new MultiFormatReader().decode(binaryBitmap);
            return Optional.ofNullable(result.getText());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

}
