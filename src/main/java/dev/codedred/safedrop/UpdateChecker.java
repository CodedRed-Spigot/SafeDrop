package dev.codedred.safedrop;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateChecker {

    private URL checkURL;
    private String newVersion;
    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
            plugin.getLogger().warning("[SafeDrop] Could not connect to spigotmc.org to check for update!");
        }
    }

    public boolean checkForUpdates() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) checkURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);

        newVersion = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                .readLine();

        connection.disconnect();

        return !plugin.getDescription().getVersion().equals(newVersion);
    }
}
