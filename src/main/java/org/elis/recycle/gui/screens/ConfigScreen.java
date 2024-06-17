package org.elis.recycle.gui.screens;

import org.elis.recycle.database.Database;
import org.elis.recycle.database.DatabaseUpdater;
import org.elis.recycle.files.Config;
import org.elis.recycle.files.ConfigFactory;
import org.elis.recycle.wifi.WiFiConnector;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;

public class ConfigScreen extends JPanel {

    private final Config config;
    private final JTextArea textArea;
    private final Database database;

    //todo: add buttons:
    // - try update database
    // - save
    // - close
    public ConfigScreen(Config config, Database database) {
        this.config = config;
        this.textArea = new JTextArea();
        this.database = database;
    }

    private void connectToWifiAndUpdateDatabase(Config config) {
        Optional<Database> newDatabase = Optional.empty();
        try {
            newDatabase = Optional.of(DatabaseUpdater.attemptUpdate());
        } catch (IOException d1) {
            try {
                WiFiConnector.connectToNetwork(config.getWifiNetwork());
            } catch (IOException w1) {
                JOptionPane.showMessageDialog(this,
                        "Failed to connect to WiFi network with name " + config.getWifiNetwork().getSsid() + "." +
                                "\n\nError: " + w1.getMessage(),
                        "Error Connecting To WiFi", JOptionPane.ERROR_MESSAGE);
            }
            try {
                newDatabase = Optional.of(DatabaseUpdater.attemptUpdate());
            } catch (IOException d2) {
                JOptionPane.showMessageDialog(this,
                        "Failed to load new database." +
                                "\n\nError: " + d2.getMessage(),
                        "Failed Loading Database", JOptionPane.ERROR_MESSAGE);
            }
        }
        newDatabase.ifPresent(newDB -> {
            String oldDatabaseVersion = database.getVersion();
            database.update(newDB);
            JOptionPane.showMessageDialog(this,
                    "Database updated from version " + oldDatabaseVersion + " to " + newDB.getVersion(),
                    "Successfully Updated Database", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void save() {
        ConfigFactory.updateConfig(textArea.getText()).ifPresent(config::update);
    }

}
