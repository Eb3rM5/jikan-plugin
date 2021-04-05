package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.entity.TimeCard;
import dev.mainardes.app.jikan.entity.JikanUser;

import java.time.LocalDateTime;
import java.util.List;

public abstract class WatchTimePlugin<T> extends PluginBase<T> {

    public abstract JikanUser getUser();

    public abstract TimeCard getTimeCard(JikanUser user, LocalDateTime current);

    public abstract List<TimeCard> getTimeCards(JikanUser user);

}
