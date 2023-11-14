package app.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigManager {
    private Properties properties;
    private String configFilePath;
    private ObjectMapper objectMapper;

    private ConfigManager(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = new Properties();
        this.objectMapper = new ObjectMapper();
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

    public RBACPolicy readRBACJson(String parameter) throws IOException {
        Map map = objectMapper.readValue(new File(getParameter(parameter)),
                Map.class);
        return new RBACPolicy(map);
    }

    public ACLPolicy readACLJson(String parameter) throws IOException {
        Map map = objectMapper.readValue(new File(getParameter(parameter)),
                Map.class);
        return new ACLPolicy(map);
    }

    public void writeJson(Map<String, ?> policyMap, String parameter) throws IOException {
        objectMapper.writeValue(new File(getParameter(parameter)), policyMap);
    }

    private void saveConfig() {
        try (FileOutputStream outStream = new FileOutputStream(configFilePath)) {
            properties.store(outStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
