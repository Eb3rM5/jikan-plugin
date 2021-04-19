package dev.mainardes.app.jikan.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TimePoint (LocalDateTime time,
                         boolean isAccepted) implements Comparable<TimePoint> {

    public TimePoint(LocalDateTime time){
        this(time, false);
    }

    @Override
    public int compareTo(TimePoint o) {
        if (o.time == null) return 1;

        if (time == null || time.isBefore(o.time)) return -1;
        else if (time.isAfter(o.time)) return 1;

        return 0;
    }

    public LocalTime toTime(){
        return time.toLocalTime();
    }

}
