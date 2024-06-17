package org.elis.recycle.database;

import org.elis.recycle.files.ApplicationFileFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DatabaseFactory {

    public static final String BACKUP_DATABASE = "{\"version\":\"0.0.0\", \"products\":[]}";

    public static Path getDatabaseFile() {
        return ApplicationFileFactory.getFile("database.json", BACKUP_DATABASE);
    }

    public static Database loadDatabaseOrBackup(Path databaseFile) {
        return ApplicationFileFactory.loadFileOrBackup(Database.class, databaseFile, BACKUP_DATABASE);
    }

    public static Database updateDatabase(String newContents) throws IOException {
        Files.writeString(getDatabaseFile(), newContents, StandardOpenOption.WRITE);
        return loadDatabaseOrBackup(getDatabaseFile());
    }

}
