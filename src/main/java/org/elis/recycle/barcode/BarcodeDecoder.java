package org.elis.recycle.barcode;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface BarcodeDecoder {

    Optional<String> decodeBarcode(BufferedImage image);

}
