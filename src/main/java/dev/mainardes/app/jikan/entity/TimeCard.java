package dev.mainardes.app.jikan.entity;

import java.time.LocalDateTime;
import java.util.List;

public class TimeCard {

    private final List<TimeRange> ranges;

    public TimeCard(List<TimeRange> ranges){
        this.ranges = ranges;
    }

    public List<TimeRange> getRanges() {
        return ranges;
    }

    public static record TimeRange(LocalDateTime start,
                                   LocalDateTime end){

    }

}
