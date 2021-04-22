package dev.mainardes.app.jikan.exception;

import dev.mainardes.app.jikan.plugin.PluginManager;

public class PluginManagerAlreadyRegistered extends PluginException {

    public PluginManagerAlreadyRegistered(){
        super("A plugin manager is already registered");
    }

}
