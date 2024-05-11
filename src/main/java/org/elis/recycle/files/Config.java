package org.elis.recycle.files;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elis.recycle.gui.FontContainer;

@AllArgsConstructor
@Getter
public class Config {

    @SerializedName("product_search_screen")
    private final ProductSearchScreenSettings productSearchScreenSettings;

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
