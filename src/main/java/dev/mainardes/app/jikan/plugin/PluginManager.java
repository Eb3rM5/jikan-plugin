package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.exception.NotRegisteredAsPluginManager;
import dev.mainardes.app.jikan.exception.PluginException;
import dev.mainardes.app.jikan.exception.PluginManagerAlreadyRegistered;
import dev.mainardes.app.jikan.exception.PluginNotFound;
import dev.mainardes.app.jikan.util.JikanPluginUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class PluginManager {

    private static PluginManager INSTANCE;

    private final Map<String, PluginBase<?>> plugins = new HashMap<>();

    protected PluginManager() throws PluginManagerAlreadyRegistered {
        if (INSTANCE != null) throw new PluginManagerAlreadyRegistered();
        INSTANCE = this;
    }

    public abstract Path getPluginDirectory();

    public Map<String, PluginBase<?>> getPlugins(){
        return plugins;
    }

    public boolean isRegistered(){
        return INSTANCE == this;
    }

    public <T extends PluginBase<?>> T getPlugin(Class<T> pluginClass) throws PluginNotFound {
        String typeName = pluginClass.getTypeName();
        PluginBase<?> registeredPlugin = plugins.get(typeName);
        if (registeredPlugin != null) return pluginClass.cast(registeredPlugin);
        else {
            T plugin = JikanPluginUtil.newInstanceOf(pluginClass);
            if (plugin != null) {
                plugins.put(pluginClass.getTypeName(), plugin);
                return plugin;
            } else throw new PluginNotFound(pluginClass);
        }
    }

    public void unregister(PluginBase<?> plugin) throws IOException, NotRegisteredAsPluginManager {
        if (INSTANCE != this) throw new NotRegisteredAsPluginManager(this);

        var propertyClass = Objects.requireNonNull(getPropertyClassName(plugin), "Couldn't determine property class type.");
        JikanPluginUtil.delete(plugin.getDirectory());

        getPlugins().remove(propertyClass);
    }

    public void uninstall() throws PluginException, IOException {
        for (var plugin : plugins.values()) {
            plugin.unregister();
        }

        Files.deleteIfExists(getPluginDirectory());
    }

    public static String getPropertyClassName(PluginBase<?> plugin){
        var type = JikanPluginUtil.getGenericTypeOf(plugin.getClass());
        return type != null ? type.getTypeName() : null;
    }

    public static PluginManager getPluginManager(){
        return INSTANCE;
    }

    public static <T extends PluginManager> T getPluginManager(Class<T> managerClass) throws NotRegisteredAsPluginManager {

        if (INSTANCE != null){
            if (managerClass.isInstance(INSTANCE)){
                return managerClass.cast(INSTANCE);
            }
            throw new NotRegisteredAsPluginManager(managerClass);
        }

        final T manager = JikanPluginUtil.newInstanceOf(managerClass);

        if (manager == null) return null;
        else {
            INSTANCE = manager;
            return manager;
        }
    }

}
