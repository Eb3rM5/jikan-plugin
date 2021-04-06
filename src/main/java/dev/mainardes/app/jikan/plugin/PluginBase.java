package dev.mainardes.app.jikan.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class PluginBase<T> {

    private static final ObjectMapper PROPERTY_MAPPER = new ObjectMapper();

    public static final Path PLUGIN_DIRECTORY = Paths.get(System.getenv("APPDATA")).resolve("jikan").resolve("plugins");

    private T properties;

    public abstract String getName();

    public abstract String getVersion();

    public abstract Class<T> getPropertiesObjectType();

    public abstract T createEmptyProperties();

    public abstract boolean validate(T properties);

    public abstract boolean install();

    public abstract boolean uninstall();

    public T getProperties() throws IOException {
        if (properties == null) {
            var mapper = getPropertyMapper();

            if (mapper != null){
                Path pluginDirectory = PLUGIN_DIRECTORY.resolve(getName());
                Path propertiesFile = pluginDirectory.resolve("properties.json");

                if (Files.exists(propertiesFile)){
                    try (var reader = Files.newBufferedReader(propertiesFile, StandardCharsets.UTF_8)){
                        properties = mapper.readValue(reader, getPropertiesObjectType());
                    }
                } else {
                    properties = saveProperties(createEmptyProperties(), mapper, pluginDirectory, propertiesFile);
                }
            }
        }
        return properties;
    }

    public T saveProperties() throws IOException {
        var pluginDirectory = PLUGIN_DIRECTORY.resolve(getName());
        return saveProperties(getProperties(), getPropertyMapper(), pluginDirectory, pluginDirectory.resolve("properties.json"));
    }

    private T saveProperties(T properties, ObjectMapper mapper, Path pluginDirectory, Path propertiesFile) throws IOException {
        Files.createDirectories(pluginDirectory);
        Files.deleteIfExists(propertiesFile);

        if (properties != null){
            try (var writer = Files.newBufferedWriter(propertiesFile, StandardCharsets.UTF_8)){
                mapper.writeValue(writer, properties);
                return properties;
            }
        }

        return null;
    }

    public void deletePropertyFile() throws IOException {
        var pluginDirectory = PLUGIN_DIRECTORY.resolve(getName());
        var propertiesFile = pluginDirectory.resolve("properties.json");
        Files.deleteIfExists(propertiesFile);
    }

    public ObjectMapper getPropertyMapper(){
        return PROPERTY_MAPPER;
    }

}
