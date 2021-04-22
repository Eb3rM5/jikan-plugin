package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.exception.NoPluginManagerRegistered;
import dev.mainardes.app.jikan.exception.NotRegisteredAsPluginManager;

import java.io.IOException;
import java.nio.file.Path;

public abstract class PluginBase<T extends PluginProperties<T, ? extends PluginBase<T>>> {

    private final PluginManager manager;
    private T properties;

    protected PluginBase() throws NoPluginManagerRegistered {
        if ((this.manager = PluginManager.getPluginManager()) == null){
            throw new NoPluginManagerRegistered(this);
        }
    }

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

    public void reset() throws IOException {
        T properties = getProperties();
        if (properties != null) properties.delete();
    }

    public void unregister() throws IOException, NotRegisteredAsPluginManager {
        manager.unregister(this);
    }

    public Path getDirectory(){
        return manager.getPluginDirectory().resolve(getName());
    }

    public abstract String getName();

    public abstract String getVersion();

    public abstract T createEmptyProperties();

    public abstract boolean install();

    public abstract boolean uninstall();

}
