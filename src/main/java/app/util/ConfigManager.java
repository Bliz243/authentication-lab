package app.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private Properties properties;
    private String configFilePath;

    public ConfigManager(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (FileInputStream inStream = new FileInputStream(configFilePath)) {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it, rethrow it, or create a default configuration)
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
            // Handle the exception (e.g., log it or rethrow it)
        }
    }
}
