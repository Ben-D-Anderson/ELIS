package org.elis.recycle.wifi;

import org.elis.recycle.files.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WiFiConnector {

    public static void connectToNetwork(Config.WifiNetwork network) throws IOException {
        writeWPASupplicant(network);
        initWiFi();
    }

    private static void initWiFi() throws IOException {
        try {
            new ProcessBuilder("/usr/bin/sudo", "/usr/sbin/dhclient", "-r", "wlan0")
                    .start().waitFor();
        } catch (InterruptedException ignored) {}
        try {
            new ProcessBuilder("/usr/bin/sudo", "/usr/sbin/ifdown", "wlan0")
                    .start().waitFor();
        } catch (InterruptedException ignored) {}
        try {
            new ProcessBuilder("/usr/bin/sudo", "/usr/sbin/ifup", "wlan0")
                    .start().waitFor();
        } catch (InterruptedException ignored) {}
        try {
            new ProcessBuilder("/usr/bin/sudo", "/usr/sbin/dhclient", "-v", "wlan0")
                    .start().waitFor();
        } catch (InterruptedException ignored) {}
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

        ProcessBuilder processBuilder = new ProcessBuilder(
                "/usr/bin/sudo", "/usr/bin/tee", "-a", "/etc/wpa_supplicant/wpa_supplicant.conf");
        Process process = processBuilder.start();
        process.getOutputStream().write(configuredWPASupplicantContents.getBytes(StandardCharsets.UTF_8));
        process.destroy();
    }

}
