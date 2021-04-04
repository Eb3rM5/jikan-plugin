package dev.mainardes.app.jikan.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface PluginBase<T> {

    ObjectMapper PROPERTY_MAPPER = new ObjectMapper();

    Path PLUGIN_DIRECTORY = Paths.get(System.getenv("APPDATA")).resolve("jikan").resolve("plugins");

    String getName();

    String getVersion();

    Class<T> getPropertiesObjectType();

    T createEmptyProperties();

    boolean validate(T properties);

    boolean install();

    boolean uninstall();

    default T getProperties() throws IOException {
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

        return null;
    }

    default ObjectMapper getPropertyMapper(){
        return PROPERTY_MAPPER;
    }

}
