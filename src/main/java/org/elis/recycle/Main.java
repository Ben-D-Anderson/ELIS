package org.elis.recycle;

import org.elis.recycle.database.Database;
import org.elis.recycle.database.DatabaseFactory;
import org.elis.recycle.gui.MainWindow;

public class Main {
    public static void main(String[] args) {
        Database database = DatabaseFactory.loadDatabaseOrBackup(DatabaseFactory.getDatabaseFile());

        MainWindow frame = new MainWindow(database);
        frame.setVisible(true);

        frame.searchForProduct();

    }
}