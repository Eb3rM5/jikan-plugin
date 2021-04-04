package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.entity.TimeCard;
import dev.mainardes.app.jikan.entity.JikanUser;

import java.time.LocalDateTime;
import java.util.List;

public interface WatchTimePlugin<T> extends PluginBase<T> {

    JikanUser getUser();

    TimeCard getTimeCard(JikanUser user, LocalDateTime current);

    List<TimeCard> getTimeCards(JikanUser user);

}
