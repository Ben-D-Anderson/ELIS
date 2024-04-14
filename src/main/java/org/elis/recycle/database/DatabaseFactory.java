package org.elis.recycle.database;

import com.google.gson.Gson;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class DatabaseFactory {

    public static final String BACKUP_DATABASE = "{\"products\":[]}";

    public static Path getDatabaseFile() {
        AppDirs appDirs = AppDirsFactory.getInstance();
        Path dataDir = Path.of(appDirs.getUserDataDir("ELIS", "0.1.0",
                "Benjamin Anderson"));
        try {
            Files.createDirectories(dataDir);
        } catch (IOException ignored) {}
        Path databaseFile = dataDir.resolve("database.json");

        boolean shouldCopyDefaults = true;
        try {
            shouldCopyDefaults = Files.notExists(databaseFile)
                    || Files.readString(databaseFile).length() <= BACKUP_DATABASE.length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (shouldCopyDefaults) {
            try {
                InputStream defaultDatabaseStream = Objects.requireNonNull(
                        DatabaseFactory.class.getResourceAsStream("/database.json"));
                Files.copy(defaultDatabaseStream, databaseFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                try {
                    Files.writeString(databaseFile, BACKUP_DATABASE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return databaseFile;
    }

    public static Database loadDatabaseOrBackup(Path databaseFile) {
        Database database;
        try (BufferedReader reader = Files.newBufferedReader(databaseFile)) {
            database = new Gson().fromJson(reader, Database.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Forced to use BACKUP_DATABASE.");
            database = new Gson().fromJson(BACKUP_DATABASE, Database.class);
        }
        return database;
    }

}
