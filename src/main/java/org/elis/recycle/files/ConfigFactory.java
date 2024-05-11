package org.elis.recycle.files;

import java.nio.file.Path;

public class ConfigFactory {

    public static final String BACKUP_CONFIG = "{}";

    public static Path getConfigFile() {
        return ApplicationFileFactory.getFile("config.json", BACKUP_CONFIG);
    }

    public static Config loadConfigOrBackup(Path configFile) {
        return ApplicationFileFactory.loadFileOrBackup(Config.class, configFile, BACKUP_CONFIG);
    }

}
