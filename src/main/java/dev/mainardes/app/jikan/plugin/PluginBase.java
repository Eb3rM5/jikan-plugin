package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.exception.NoPluginManagerRegistered;
import dev.mainardes.app.jikan.exception.NotRegisteredAsPluginManager;
import dev.mainardes.app.jikan.util.JikanPluginUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public abstract class PluginBase<T extends PluginProperties<T, ? extends PluginBase<T>>> {

    private final PluginManager manager;
    private T properties;

    private final Path directory;

    protected PluginBase() throws NoPluginManagerRegistered {
        if ((this.manager = PluginManager.getPluginManager()) == null){
            throw new NoPluginManagerRegistered(this);
        }

        directory = manager.getPluginDirectory().resolve(JikanPluginUtil.generateUUIDFromString(getName()));
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
        if (properties != null) properties.delete();
        properties = null;
    }

    public void unregister() throws IOException, NotRegisteredAsPluginManager {
        manager.unregister(this);
    }

    public Path getDirectory(){
        return directory;
    }

    public abstract String getName();

    public abstract String getVersion();

    public abstract T createEmptyProperties();

    public abstract boolean install();

    public abstract boolean uninstall();

}
