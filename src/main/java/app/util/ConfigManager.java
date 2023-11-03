package app.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private Properties properties;
    private String configFilePath;

    private ConfigManager(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = new Properties();
        loadConfig();
    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager("config.properties");
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    private void loadConfig() {
    try (InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(configFilePath)) {
        if (inStream == null) {
            throw new FileNotFoundException("Resource file not found: " + configFilePath);
        }
        properties.load(inStream);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public String getParameter(String parameter) {
        return properties.getProperty(parameter);
    }

    public void setParameter(String parameter, String value) {
        properties.setProperty(parameter, value);
        saveConfig();
    }

    private void saveConfig() {
        try (FileOutputStream outStream = new FileOutputStream(configFilePath)) {
            properties.store(outStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
