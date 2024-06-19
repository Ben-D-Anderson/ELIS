package org.elis.recycle.files;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elis.recycle.gui.FontContainer;

import java.io.Serializable;
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

    public void update(Config newConfig) {
        this.wifiNetwork = newConfig.wifiNetwork;
        this.bins = newConfig.bins;
        this.userInterfaceSettings = newConfig.userInterfaceSettings;
    }

    @SerializedName("wifi_network")
    private WifiNetwork wifiNetwork;

    @SerializedName("bins")
    private List<Bin> bins;

    @SerializedName("user_interface")
    private UserInterfaceSettings userInterfaceSettings;

    @AllArgsConstructor
    @Getter
    public static class UserInterfaceSettings {

        @SerializedName("product_screen")
        private ProductScreenSettings productScreenSettings;

        @SerializedName("main_menu_screen")
        private MainMenuScreenSettings mainMenuScreenSettings;

        @SerializedName("loading_screen")
        private LoadingScreenSettings loadingScreenSettings;

        @SerializedName("config_screen")
        private ConfigScreenSettings configScreenSettings;

        @SerializedName("product_search_screen")
        private ProductSearchScreenSettings productSearchScreenSettings;

        @SerializedName("barcode_scanning_screen")
        private BarcodeScanningScreenSettings barcodeScanningScreenSettings;

        @SerializedName("pop_ups")
        private PopUpSettings popUpSettings;

        @SerializedName("use_system_theme")
        private boolean useSystemTheme;

    }

    @AllArgsConstructor
    @Getter
    public static class PopUpSettings {

        @SerializedName("height")
        private final int height;
        @SerializedName("width")
        private final int width;
        @SerializedName("message_font_size")
        private final int messageFontSize;
        @SerializedName("button_font_size")
        private final int buttonFontSize;

    }

    @AllArgsConstructor
    @Getter
    public static class ProductScreenSettings {

        @SerializedName("step_font")
        private final FontContainer stepFont;
        @SerializedName("product_name_font")
        private final FontContainer productNameFont;
        @SerializedName("begin_recycle_button_font")
        private final FontContainer beginRecycleButtonFont;
        @SerializedName("back_button_font")
        private final FontContainer backButtonFont;
        @SerializedName("dispose_button_font")
        private final FontContainer disposeButtonFont;
        @SerializedName("next_step_button_font")
        private final FontContainer nextStepButtonFont;
        @SerializedName("finish_button_font")
        private final FontContainer finishButtonFont;

    }

    @AllArgsConstructor
    @Getter
    public static class MainMenuScreenSettings {

        @SerializedName("button_shrink_factor")
        private final int buttonShrinkFactor;

    }

    @AllArgsConstructor
    @Getter
    public static class LoadingScreenSettings {

        @SerializedName("text_font")
        private final FontContainer textFont;

    }

    @AllArgsConstructor
    @Getter
    public static class WifiNetwork implements Serializable {

        @SerializedName("name")
        private final String ssid;
        @SerializedName("password")
        private final String password;

    }

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
    public static class ConfigScreenSettings {

        @SerializedName("text_box_font")
        private final FontContainer textBoxFont;
        @SerializedName("update_database_button_font")
        private final FontContainer updateDatabaseButtonFont;
        @SerializedName("save_button_font")
        private final FontContainer saveButtonFont;
        @SerializedName("cancel_button_font")
        private final FontContainer cancelButtonFont;

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
        @SerializedName("product_option_background_override")
        private final String productOptionBackground;
        @SerializedName("product_option_foreground_override")
        private final String productOptionForeground;

    }

}
