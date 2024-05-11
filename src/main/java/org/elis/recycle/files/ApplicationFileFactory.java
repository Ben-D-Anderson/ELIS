package org.elis.recycle.files;

import com.google.gson.Gson;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ApplicationFileFactory {

    public static Path getFile(String fileName, String backupContents) {
        AppDirs appDirs = AppDirsFactory.getInstance();
        Path dataDir = Path.of(appDirs.getUserDataDir("ELIS", "0.1.0",
                "Benjamin Anderson"));
        try {
            Files.createDirectories(dataDir);
        } catch (IOException ignored) {}
        Path file = dataDir.resolve(fileName);

        boolean shouldCopyDefaults = false;
        try {
            shouldCopyDefaults = Files.notExists(file) || Files.size(file) <= 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (shouldCopyDefaults) {
            try (InputStream defaultDatabaseStream = Objects.requireNonNull(
                    ApplicationFileFactory.class.getResourceAsStream("/" + fileName))) {
                Files.copy(defaultDatabaseStream, file, StandardCopyOption.REPLACE_EXISTING);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                try {
                    Files.writeString(file, backupContents);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static <T> T loadFileOrBackup(Class<T> type, Path file, String backupContents) {
        T database;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            database = new Gson().fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading the application file '" + file.getFileName() + "'.");
            System.err.println("Forced to use backup file contents.");
            database = new Gson().fromJson(backupContents, type);
        }
        return database;
    }

}
