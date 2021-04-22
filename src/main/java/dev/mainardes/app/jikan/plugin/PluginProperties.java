package dev.mainardes.app.jikan.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.mainardes.app.jikan.exception.PluginException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface PluginProperties<T extends PluginProperties<T, P>, P extends PluginBase<T>> {

    ObjectMapper PROPERTY_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    default T save() throws IOException {
        var file = getPropertyFile();
        Files.createDirectories(file.getParent());

        Files.deleteIfExists(file);

        try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)){
            PROPERTY_MAPPER.writeValue(writer, this);
            return refresh();
        }
    }

    default T refresh() throws IOException {
        var file = getPropertyFile();
        if (Files.exists(file)){
            try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)){
                return PROPERTY_MAPPER.readerForUpdating(this).readValue(reader);
            }
        } else {
            Files.createDirectories(getPropertyFile().getParent());
            try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)){
                var empty = empty();
                PROPERTY_MAPPER.writeValue(writer, empty());
                return empty;
            }
        }
    }

    default void delete() throws IOException {
        var file = getPropertyFile();
        Files.deleteIfExists(file);
    }

    @JsonIgnore
    default Path getPropertyFile(){
        var plugin = Objects.requireNonNull(getPlugin(), "Plugin can't be null!");
        return plugin.getDirectory().resolve("properties.json");
    }

    @JsonIgnore
    default P getPlugin(){
        final var manager = PluginManager.getPluginManager();
        try {
            return getPluginClass().cast(manager.getPlugin(getPluginClass()));
        } catch (PluginException e){
            return null;
        }
    }

    @JsonIgnore
    Class<P> getPluginClass();

    @JsonIgnore
    T empty();

    @JsonIgnore
    boolean isValid();

}
