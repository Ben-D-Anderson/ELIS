package org.elis.recycle.wifi;

import org.elis.recycle.files.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WiFiConnector {

    public static void connectToNetwork(Config.WifiNetwork network) throws IOException {
        writeWPASupplicant(network);
        initWiFi();
    }

    private static void initWiFi() throws IOException {
        Runtime.getRuntime().exec("/usr/sbin/dhclient -r wlp0s20f3");
        Runtime.getRuntime().exec("/usr/sbin/ifdown wlp0s20f3");
        Runtime.getRuntime().exec("/usr/sbin/ifup wlp0s20f3");
        Runtime.getRuntime().exec("/usr/sbin/dhclient -v wlp0s20f3");
    }

    private static final String WPASupplicantContents =
            "country=gb\n" +
            "update_config=1\n" +
            "ctrl_interface=/var/run/wpa_supplicant\n" +
            "\n" +
            "network={\n" +
            " scan_ssid=1\n" +
            " ssid=\"{SSID}\"\n" +
            " psd=\"{PASSWORD}\"\n" +
            "}";
    private static void writeWPASupplicant(Config.WifiNetwork network) throws IOException {
        String configuredWPASupplicantContents = WPASupplicantContents
                .replace("{SSID}", network.getSsid())
                .replace("{PASSWORD}", network.getPassword());
        Files.writeString(Paths.get("/etc/wpa_supplicant/wpa_supplicant.conf"), configuredWPASupplicantContents,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

}
