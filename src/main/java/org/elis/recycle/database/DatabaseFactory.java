package org.elis.recycle.database;

import org.elis.recycle.files.ApplicationFileFactory;

import java.nio.file.Path;

public class DatabaseFactory {

    public static final String BACKUP_DATABASE = "{\"products\":[]}";

    public static Path getDatabaseFile() {
        return ApplicationFileFactory.getFile("database.json", BACKUP_DATABASE);
    }

    public static Database loadDatabaseOrBackup(Path databaseFile) {
        return ApplicationFileFactory.loadFileOrBackup(Database.class, databaseFile, BACKUP_DATABASE);
    }

}
