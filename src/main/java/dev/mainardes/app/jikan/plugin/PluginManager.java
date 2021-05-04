package dev.mainardes.app.jikan.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.mainardes.app.jikan.exception.NotRegisteredAsPluginManager;
import dev.mainardes.app.jikan.exception.PluginNotFound;
import dev.mainardes.app.jikan.util.JikanPluginUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class PluginManager {

    private static PluginManager INSTANCE;

    private final Map<String, PluginBase<?>> plugins = new HashMap<>();

    public abstract Path getPluginDirectory();

    public Map<String, PluginBase<?>> getPlugins(){
        return plugins;
    }

    public List<WatchTimePlugin<?>> getWatchTimePlugins(){
        return getPlugins().values()
                            .parallelStream()
                            .filter(WatchTimePlugin.class::isInstance)
                            .map(e->(WatchTimePlugin<?>)e)
                            .collect(Collectors.toList());
    }

    public <T extends PluginBase<?>> T getPlugin(String name, Class<T> pluginType){
        for (var plugin : getPlugins().values()){
            if (plugin.getName().equals(name) && pluginType.isInstance(plugin)){
                return pluginType.cast(plugin);
            }
        }
        return null;
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

        var pluginClass = plugin.getClass().getTypeName();
        plugin.uninstall();

        plugin = getPlugins().remove(pluginClass);
        JikanPluginUtil.delete(plugin.getDirectory());
    }

    public void install() {
    }

    public void uninstall() throws IOException, NotRegisteredAsPluginManager {
        for (var plugin : plugins.values()) {
            plugin.unregister();
        }

        JikanPluginUtil.delete(getPluginDirectory());
    }

    public static <T extends PluginManager> T getPluginManager(Class<T> managerClass) throws NotRegisteredAsPluginManager {
        if (INSTANCE != null){
            if (managerClass.isInstance(INSTANCE)){
                return managerClass.cast(INSTANCE);
            }
            throw new NotRegisteredAsPluginManager(managerClass);
        } else {
            final T manager = JikanPluginUtil.newInstanceOf(managerClass);

            if (manager == null) return null;
            else {
                INSTANCE = manager;
                manager.install();
                return manager;
            }
        }
    }

    public boolean isRegistered(){
        return INSTANCE == this;
    }

    public static PluginManager getPluginManager(){
        return INSTANCE;
    }

}
