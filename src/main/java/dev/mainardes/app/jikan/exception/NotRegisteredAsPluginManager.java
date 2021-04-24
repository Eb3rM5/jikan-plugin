package dev.mainardes.app.jikan.exception;

import dev.mainardes.app.jikan.plugin.PluginManager;

public class NotRegisteredAsPluginManager extends PluginException {

    public NotRegisteredAsPluginManager(Class<? extends PluginManager> managerClass){
        super(managerClass.getTypeName() + " is not the plugin manager");
    }

    public NotRegisteredAsPluginManager(PluginManager manager){
        super(manager + " is not the plugin manager");
    }

}
