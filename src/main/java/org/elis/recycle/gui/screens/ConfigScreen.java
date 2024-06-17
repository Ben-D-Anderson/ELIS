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
    private final VirtualKeyboard virtualKeyboard;

    public ConfigScreen(MainWindow mainWindow, Config config, Database database) {
        super();
        this.config = config;
        this.textArea = new JTextArea();
        this.database = database;
        this.virtualKeyboard = new VirtualKeyboard();
        final ConfigScreen thisInstance = this;

        textArea.setFont(config.getConfigScreenSettings().getTextBoxFont().asFont());

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
                mainWindow.loadingScreen();
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
            public void keyTyped(KeyEvent e) {
                updateDatabaseButton.setEnabled(false);
                saveButton.setEnabled(true);
            }
        });
        JButton cancelButton = new JButton("Cancel");
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

        updateDatabaseButton.setFont(config.getConfigScreenSettings().getUpdateDatabaseButtonFont().asFont());
        saveButton.setFont(config.getConfigScreenSettings().getSaveButtonFont().asFont());
        cancelButton.setFont(config.getConfigScreenSettings().getCancelButtonFont().asFont());

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

    private Runnable connectToWifiAndUpdateDatabase(JFrame parentFrame, Config config) {
        Database newDatabase;
        try {
            newDatabase = DatabaseUpdater.attemptUpdate();
        } catch (IOException d1) {
            try {
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
                        "Failed to load new database." +
                                "\n\nError: " + d2,
                        "Failed Loading Database", JOptionPane.ERROR_MESSAGE);
            }
        }
        String oldDatabaseVersion = database.getVersion();
        String newDatabaseVersion = newDatabase.getVersion();
        if (oldDatabaseVersion.equals(newDatabaseVersion)) {
            return () -> JOptionPane.showMessageDialog(parentFrame,
                    "Database was not updated because the remote database has the same version " +
                            "as the local database (" + oldDatabaseVersion + ").",
                    "Database Not Updated", JOptionPane.WARNING_MESSAGE);
        }
        database.update(newDatabase);
        return () -> JOptionPane.showMessageDialog(parentFrame,
                    "Database updated from version " + oldDatabaseVersion + " to " + newDatabaseVersion,
                    "Successfully Updated Database", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshFromConfig() throws IOException {
        String configContent = Files.readString(ConfigFactory.getConfigFile());
        textArea.setText(configContent);
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
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
                "The config has been saved and applied.",
                "Successfully Updated Config", JOptionPane.INFORMATION_MESSAGE);
        saveButton.setEnabled(false);
        updateDatabaseButton.setEnabled(true);
    }

}
