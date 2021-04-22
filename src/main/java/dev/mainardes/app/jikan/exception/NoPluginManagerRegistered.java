package dev.mainardes.app.jikan.exception;

import dev.mainardes.app.jikan.plugin.PluginBase;

public class NoPluginManagerRegistered extends PluginException {

    public NoPluginManagerRegistered(PluginBase<?> plugin){
        super(plugin + " can't be instantiated because there's no registered plugin manager");
    }

}
