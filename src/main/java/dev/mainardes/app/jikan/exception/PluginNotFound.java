package dev.mainardes.app.jikan.exception;

import dev.mainardes.app.jikan.plugin.PluginBase;

public class PluginNotFound extends PluginException {

    public PluginNotFound(Class<? extends PluginBase<?>> pluginClass){
        super("There is no plugin " + pluginClass.getTypeName() + " for the specified class.");
    }

}
