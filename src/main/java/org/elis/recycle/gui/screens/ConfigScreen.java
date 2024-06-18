package org.elis.recycle.gui.screens;

import org.elis.recycle.database.Database;
import org.elis.recycle.database.DatabaseUpdater;
import org.elis.recycle.files.ApplicationFileFactory;
import org.elis.recycle.files.Config;
import org.elis.recycle.files.ConfigFactory;
import org.elis.recycle.gui.MainWindow;
import org.elis.recycle.gui.VirtualKeyboard;
import org.elis.recycle.wifi.WiFiConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigScreen extends JPanel {

    private final Config config;
    private final JTextArea textArea;
    private final Database database;
    private final JScrollPane scrollPane;
    private final JButton updateDatabaseButton = new JButton("Update Database");
    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");
    private final VirtualKeyboard virtualKeyboard;
    private String oldConfigString;

    public ConfigScreen(MainWindow mainWindow, Config config, Database database) {
        super();
        this.config = config;
        this.textArea = new JTextArea();
        this.database = database;
        this.virtualKeyboard = new VirtualKeyboard();
        final ConfigScreen thisInstance = this;

        textArea.setFont(config.getUserInterfaceSettings()
                .getConfigScreenSettings()
                .getTextBoxFont().asFont());

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints top = new GridBagConstraints();
        top.anchor = GridBagConstraints.NORTH;
        top.weightx = 1;
        top.fill = GridBagConstraints.BOTH;
        top.gridy = 1;
        top.weighty = 10;

        this.scrollPane = new JScrollPane(textArea);
        add(scrollPane, top);

        JPanel buttonSection = new JPanel();
        updateDatabaseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!updateDatabaseButton.isEnabled()) return;
                mainWindow.loadingScreen("Downloading Database...");
                new Thread(() -> {
                    Runnable toInvoke = connectToWifiAndUpdateDatabase(mainWindow, config);
                    SwingUtilities.invokeLater(() -> {
                        mainWindow.configScreen();
                        toInvoke.run();
                    });
                }).start();
            }
        });
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    virtualKeyboard.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(thisInstance,
                            "Failed to open on-screen keyboard.",
                            "Virtual Keyboard Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!saveButton.isEnabled()) return;
                save();
            }
        });
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!textArea.getText().equals(oldConfigString)) {
                    updateDatabaseButton.setEnabled(false);
                    saveButton.setEnabled(true);
                } else {
                    updateDatabaseButton.setEnabled(true);
                    saveButton.setEnabled(false);
                }
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                virtualKeyboard.close();
                mainWindow.mainScreen();
            }
        });

        updateDatabaseButton.setFocusPainted(false);
        saveButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        updateDatabaseButton.setFont(config.getUserInterfaceSettings()
                .getConfigScreenSettings()
                .getUpdateDatabaseButtonFont().asFont());
        saveButton.setFont(config.getUserInterfaceSettings()
                .getConfigScreenSettings()
                .getSaveButtonFont().asFont());
        cancelButton.setFont(config.getUserInterfaceSettings()
                .getConfigScreenSettings()
                .getCancelButtonFont().asFont());

        buttonSection.add(updateDatabaseButton);
        buttonSection.add(saveButton);
        buttonSection.add(cancelButton);

        GridBagConstraints bottom = new GridBagConstraints();
        bottom.fill = GridBagConstraints.BOTH;
        bottom.gridy = 2;
        bottom.weightx = 1;
        bottom.weighty = 1;
        add(buttonSection, bottom);
    }

    private Runnable connectToWifiAndUpdateDatabase(MainWindow parentFrame, Config config) {
        Database newDatabase;
        try {
            newDatabase = DatabaseUpdater.attemptUpdate();
        } catch (IOException d1) {
            try {
                if (config.getWifiNetwork().getSsid().isEmpty()) {
                    return () -> JOptionPane.showMessageDialog(parentFrame,
                            "Please specify a WiFi network in the config.",
                            "Error Connecting To WiFi", JOptionPane.ERROR_MESSAGE);
                }
                WiFiConnector.connectToNetwork(config.getWifiNetwork());
            } catch (IOException w1) {
                return () -> JOptionPane.showMessageDialog(parentFrame,
                        "Failed to connect to WiFi network with name '" + config.getWifiNetwork().getSsid() + "'." +
                                "\n\nError: " + w1,
                        "Error Connecting To WiFi", JOptionPane.ERROR_MESSAGE);
            }
            try {
                newDatabase = DatabaseUpdater.attemptUpdate();
            } catch (IOException d2) {
                return () -> JOptionPane.showMessageDialog(parentFrame,
                        "WiFi connection has been configured, please reboot your device.",
                        "Restart Device", JOptionPane.WARNING_MESSAGE);
            }
        }
        String oldDatabaseVersion = database.getVersion();
        String newDatabaseVersion = newDatabase.getVersion();
        if (oldDatabaseVersion.equals(newDatabaseVersion)) {
            return () -> JOptionPane.showMessageDialog(parentFrame,
                    "Database is already up to date.",
                    "Database Not Updated", JOptionPane.INFORMATION_MESSAGE);
        }
        database.update(newDatabase);
        return () -> JOptionPane.showMessageDialog(parentFrame,
                    "Database updated from version " + oldDatabaseVersion + " to " + newDatabaseVersion,
                    "Successfully Updated Database", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshFromConfig() throws IOException {
        String configContent = Files.readString(ConfigFactory.getConfigFile());
        textArea.setText(configContent);
        oldConfigString = configContent;
        SwingUtilities.invokeLater(() -> {
            cancelButton.requestFocus();
            scrollPane.getVerticalScrollBar().setValue(0);
        });
        updateDatabaseButton.setEnabled(true);
        saveButton.setEnabled(false);
    }

    public void save() {
        if (!ApplicationFileFactory.isJsonValid(Config.class, textArea.getText())) {
            JOptionPane.showMessageDialog(this,
                    "The config has errors in it which makes it impossible to parse.",
                    "Failed To Updated Config", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ConfigFactory.updateConfig(textArea.getText()).ifPresent(config::update);
        JOptionPane.showMessageDialog(this,
                "The config has been saved.",
                "Successfully Updated Config", JOptionPane.INFORMATION_MESSAGE);
        saveButton.setEnabled(false);
        updateDatabaseButton.setEnabled(true);
    }

}
