package dev.mainardes.app.jikan.entity;

import dev.mainardes.app.jikan.plugin.WatchTimePlugin;
import dev.mainardes.app.jikan.util.JikanPluginUtil;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeCard {

    private final LocalDate date;
    private final TimeRange[] ranges;

    public TimeCard(LocalDate date, TimeRange[] ranges){
        this.date = date;
        this.ranges = ranges;
    }

    public TimeCard(TimeRange[] ranges){
        this(null, ranges);
    }

    public LocalDate getDate() {
        return date;
    }

    public TimeRange[] getRanges() {
        return ranges;
    }

    public static record TimeRange(LocalTime start,
                                   LocalTime end){

        public boolean isEndHit(LocalTime time, JikanUser user, WatchTimePlugin<?> plugin){
            var limit = end.plus(plugin.getTimePointLimit(user));
            return JikanPluginUtil.isBetween(time, end, limit);
        }

        public boolean isStartHit(LocalTime time, JikanUser user, WatchTimePlugin<?> plugin){
            var limit = start.plus(plugin.getTimePointLimit(user));
            return JikanPluginUtil.isBetween(time, start, limit);
        }

        public boolean isBetween(LocalTime time){
            return JikanPluginUtil.isBetween(time, start, end);
        }

    }

}
