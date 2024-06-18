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
        Runtime.getRuntime().exec("/usr/sbin/dhclient -r wlan0");
        Runtime.getRuntime().exec("/usr/sbin/ifdown wlan0");
        Runtime.getRuntime().exec("/usr/sbin/ifup wlan0");
        Runtime.getRuntime().exec("/usr/sbin/dhclient -v wlan0");
    }

    private static final String WPASupplicantContents =
            "country=gb\n" +
            "update_config=1\n" +
            "ctrl_interface=/var/run/wpa_supplicant GROUP=netdev\n" +
            "\n" +
            "network={\n" +
            " scan_ssid=1\n" +
            " ssid=\"{SSID}\"\n" +
            " psk=\"{PASSWORD}\"\n" +
            "}";
    private static void writeWPASupplicant(Config.WifiNetwork network) throws IOException {
        String configuredWPASupplicantContents = WPASupplicantContents
                .replace("{SSID}", network.getSsid())
                .replace("{PASSWORD}", network.getPassword());
        Files.writeString(Paths.get("/etc/wpa_supplicant/wpa_supplicant.conf"), configuredWPASupplicantContents,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

}
