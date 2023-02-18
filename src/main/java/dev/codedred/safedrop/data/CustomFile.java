package dev.codedred.safedrop.data;

import dev.codedred.safedrop.SafeDrop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class CustomFile {
    private final String name;
    private final SafeDrop plugin;
    private FileConfiguration dataConfig = null;
    private File dataConfigFile = null;

    public CustomFile(SafeDrop plugin, String fileName) {
        this.plugin = plugin;
        this.name = fileName;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.dataConfigFile == null)
            this.dataConfigFile = new File(this.plugin.getDataFolder(), name);

        this.dataConfig = YamlConfiguration.loadConfiguration(this.dataConfigFile);

        InputStream defConfigStream = this.plugin.getResource(name);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration
                    .loadConfiguration(new InputStreamReader(defConfigStream));
            this.dataConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null)
            reloadConfig();
        return this.dataConfig;
    }

    public void saveConfig() {
        if ((this.dataConfig == null) || (this.dataConfigFile == null))
            return;
        try {
            getConfig().save(this.dataConfigFile);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, ex, () -> "Could not save config to " + this.dataConfigFile);        }
    }

    public void saveDefaultConfig() {
        if (this.dataConfigFile == null)
            this.dataConfigFile = new File(this.plugin.getDataFolder(), name);
        if (!this.dataConfigFile.exists())
            this.plugin.saveResource(name, false);
    }

}