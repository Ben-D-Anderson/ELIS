package org.elis.recycle.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class ConfigFactory {

    public static final String BACKUP_CONFIG = "{}";

    public static Path getConfigFile() {
        return ApplicationFileFactory.getFile("config.json", BACKUP_CONFIG);
    }

    public static Config loadConfigOrBackup(Path configFile) {
        return ApplicationFileFactory.loadFileOrBackup(Config.class, configFile, BACKUP_CONFIG);
    }

    public static Optional<Config> updateConfig(String newContents) {
        try {
            Files.writeString(getConfigFile(), newContents, StandardOpenOption.WRITE);
            return Optional.of(loadConfigOrBackup(getConfigFile()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
