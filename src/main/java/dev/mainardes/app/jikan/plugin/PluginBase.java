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
                        return mapper.readValue(reader, getPropertiesObjectType());
                    }
                } else {
                    Files.createDirectories(pluginDirectory);
                    T emptyPropertyObject = createEmptyProperties();

                    if (emptyPropertyObject != null){
                        try (var writer = Files.newBufferedWriter(propertiesFile, StandardCharsets.UTF_8)){
                            mapper.writeValue(writer, emptyPropertyObject);
                            return emptyPropertyObject;
                        }
                    }

                }
            }
        }
        return properties;
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
