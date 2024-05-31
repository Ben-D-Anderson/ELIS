package org.elis.recycle.files;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elis.recycle.gui.FontContainer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Getter
public class Config {

    public Optional<Bin> getMaterialBin(String material) {
        return bins.stream()
                .filter(bin -> bin.getMaterials().contains(material))
                .findFirst();
    }

    @SerializedName("bins")
    private final List<Bin> bins;

    @SerializedName("product_search_screen")
    private final ProductSearchScreenSettings productSearchScreenSettings;

    @SerializedName("barcode_scanning_screen")
    private final BarcodeScanningScreenSettings barcodeScanningScreenSettings;

    @AllArgsConstructor
    @Getter
    public static class Bin {

        @SerializedName("name")
        private final String name;
        @SerializedName("angle")
        private final double angle;
        @SerializedName("materials")
        private final Set<String> materials;

    }

    @AllArgsConstructor
    @Getter
    public static class BarcodeScanningScreenSettings {

        @SerializedName("scanning_timeout_seconds")
        private final int scanningTimeoutSeconds;
        @SerializedName("mirror_image")
        private final boolean mirrorImage;

    }

    @AllArgsConstructor
    @Getter
    public static class ProductSearchScreenSettings {

        @SerializedName("back_button_font")
        private final FontContainer backButtonFont;
        @SerializedName("input_box_font")
        private final FontContainer inputBoxFont;
        @SerializedName("product_option_font")
        private final FontContainer productOptionFont;

    }

}
