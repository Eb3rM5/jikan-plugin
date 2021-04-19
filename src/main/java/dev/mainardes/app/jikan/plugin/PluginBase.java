package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.util.JikanUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class PluginBase<T extends PluginProperties<T, ? extends PluginBase<T>>> {

    public static final Path PLUGIN_DIRECTORY = Paths.get(System.getenv("APPDATA")).resolve("jikan").resolve("plugins");

    private static final Map<String, PluginBase<?>> PLUGINS = new LinkedHashMap<>();

    private T properties;

    public T getProperties() {
        if (properties == null){
            try {
                properties = createEmptyProperties().refresh();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return properties;
    }

    public void register(){
        register(this);
    }

    public void unregister() throws IOException {
        unregister(this);
    }

    public Path getDirectory(){
        return PLUGIN_DIRECTORY.resolve(getName());
    }

    public abstract String getName();

    public abstract String getVersion();

    public abstract T createEmptyProperties();

    public abstract boolean install();

    public abstract boolean uninstall();

    public static void register(PluginBase<?> plugin){
        var propertyClass = Objects.requireNonNull(getPropertyClassName(plugin), "Couldn't determine property class type.");
        PLUGINS.putIfAbsent(propertyClass, plugin);
    }

    public static void unregister(PluginBase<?> plugin) throws IOException {
        var propertyClass = Objects.requireNonNull(getPropertyClassName(plugin), "Couldn't determine property class type.");
        JikanUtil.delete(plugin.getDirectory());

        PLUGINS.remove(propertyClass);
    }

    public static <T extends PluginProperties<?, ?>> PluginBase<?> getPlugin(Class<T> propertyClass){
        return PLUGINS.get(propertyClass.getName());
    }

    public static String getPropertyClassName(PluginBase<?> plugin){
        var type = JikanUtil.getGenericTypeOf(plugin.getClass());
        return type != null ? type.getTypeName() : null;
    }

}
