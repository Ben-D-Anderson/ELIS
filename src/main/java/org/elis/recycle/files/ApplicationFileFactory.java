package org.elis.recycle.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        T value;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            value = new GsonBuilder()
                    .setLenient()
                    .create().fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading the application file '" + file.getFileName() + "'.");
            System.err.println("Forced to use backup file contents.");
            value = new Gson().fromJson(backupContents, type);
        }
        return value;
    }

    public static <T> boolean isJsonValid(Class<T> type, String jsonString) {
        try {
            T value = new GsonBuilder()
                    .setLenient()
                    .create().fromJson(jsonString, type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
