package org.elis.recycle.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DatabaseUpdater {

    private static final String updateURL = "https://pastebin.com/raw/Swu6tzKB"; //TODO

    public static Database attemptUpdate() throws IOException {
        StringBuilder builder = getUpdatePageContents();
        return DatabaseFactory.updateDatabase(builder.toString());
    }

    private static StringBuilder getUpdatePageContents() throws IOException {
        URLConnection urlConnection = new URL(updateURL).openConnection();
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(3000);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            builder.append(line).append("\n");
            line = reader.readLine();
        }
        return builder;
    }

}