package org.elis.recycle.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DatabaseUpdater {

    private static final String updateURL = ""; //TODO

    public static Database attemptUpdate() throws IOException {
        StringBuilder builder = getUpdatePageContents();
        return DatabaseFactory.updateDatabase(builder.toString());
    }

    private static StringBuilder getUpdatePageContents() throws IOException {
        URLConnection urlConnection = new URL(updateURL).openConnection();
        urlConnection.setConnectTimeout(1000);
        urlConnection.setReadTimeout(1000);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        do {
            line = reader.readLine();
            builder.append(line);
        } while (line != null);
        return builder;
    }

}