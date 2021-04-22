package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.exception.NotRegisteredAsPluginManager;
import dev.mainardes.app.jikan.exception.PluginNotFound;
import dev.mainardes.app.jikan.util.JikanUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PluginManager {

    private static PluginManager INSTANCE;

    private final Map<String, PluginBase<?>> plugins;
    private final Path directory;

    private PluginManager() {
        plugins = new HashMap<>();
        directory = Paths.get(System.getenv("APPDATA")).resolve("jikan").resolve("plugins");
    }

    public Path getPluginDirectory(){
        return directory;
    }

    public Map<String, PluginBase<?>> getPlugins(){
        return plugins;
    }

    public <T extends PluginBase<?>> T getPlugin(Class<T> pluginClass) throws PluginNotFound {

        String typeName = pluginClass.getTypeName();
        PluginBase<?> registeredPlugin = plugins.get(typeName);
        if (registeredPlugin != null) return pluginClass.cast(registeredPlugin);
        else {

            try {
                var constructors = pluginClass.getDeclaredConstructors();
                if (constructors.length > 0){
                    var constructor = constructors[0];
                    T plugin = pluginClass.cast(constructor.newInstance());
                    plugins.put(typeName, plugin);
                    return plugin;
                }
            } catch (ReflectiveOperationException e){
                e.printStackTrace();
                throw new PluginNotFound(pluginClass);
            }

        }

        throw new PluginNotFound(pluginClass);
    }

    public void unregister(PluginBase<?> plugin) throws IOException, NotRegisteredAsPluginManager {
        if (INSTANCE != this) throw new NotRegisteredAsPluginManager(this);

        var propertyClass = Objects.requireNonNull(getPropertyClassName(plugin), "Couldn't determine property class type.");
        JikanUtil.delete(plugin.getDirectory());

        getPlugins().remove(propertyClass);
    }

    public static String getPropertyClassName(PluginBase<?> plugin){
        var type = JikanUtil.getGenericTypeOf(plugin.getClass());
        return type != null ? type.getTypeName() : null;
    }

    public static PluginManager getPluginManager(){
        if (INSTANCE == null){
            INSTANCE = new PluginManager();
        }
        return INSTANCE;
    }

}
