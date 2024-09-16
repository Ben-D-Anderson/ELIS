package org.elis.recycle;

import org.elis.recycle.bin.Disposer;
import org.elis.recycle.database.Database;
import org.elis.recycle.database.DatabaseFactory;
import org.elis.recycle.files.Config;
import org.elis.recycle.files.ConfigFactory;
import org.elis.recycle.gui.MainWindow;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Config config = ConfigFactory.loadConfigOrBackup(ConfigFactory.getConfigFile());
        Database database = DatabaseFactory.loadDatabaseOrBackup(DatabaseFactory.getDatabaseFile());

        MainWindow mainWindow = new MainWindow(config, database);
        mainWindow.mainScreen();
        mainWindow.setVisible(true);

        new Thread(() -> {
            try {
                Disposer.reset();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}