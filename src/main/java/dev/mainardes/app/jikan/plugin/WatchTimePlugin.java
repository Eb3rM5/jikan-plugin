package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.entity.JikanUser;
import dev.mainardes.app.jikan.entity.TimeCard;
import dev.mainardes.app.jikan.entity.TimePoint;
import dev.mainardes.app.jikan.exception.NoPluginManagerRegistered;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WatchTimePlugin<T extends PluginProperties<T, ? extends WatchTimePlugin<T>>> extends PluginBase<T> {

    public WatchTimePlugin() throws NoPluginManagerRegistered {
        super();
    }

    public abstract JikanUser getUser();

    public abstract TimeCard getTimeCard(JikanUser user, LocalDateTime current);

    public abstract List<TimeCard> getTimeCards(JikanUser user);

    public abstract List<TimePoint> getTimePoints(JikanUser user);

    public abstract List<TimePoint> getTimePoints(JikanUser user, LocalDateTime current);

    public abstract List<TimePoint> getTimePointsBetween(JikanUser user, LocalDateTime start, LocalDateTime end);

    public abstract TimeCard.TimeRange[] getTimeRanges(JikanUser user);

    public abstract Duration getTimePointLimit(JikanUser user);

    public List<TimePoint> getTimePointsOn(JikanUser user, LocalDate date){
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return getTimePointsBetween(user, start, end);
    }

    public List<LocalTime> getTimeTableOn(JikanUser user){
        return getAdjustedTimeTableOn(user, LocalDate.now());
    }

    public List<LocalTime> getAdjustedTimeTableOn(JikanUser user, LocalDate date){
        var points = getTimePointsOn(user, date);
        var ranges = getTimeRanges(user);
        return getAdjustedTimeTableOn(points, ranges);
    }

    public List<LocalTime> getAdjustedTimeTableOn(List<TimePoint> points, TimeCard.TimeRange[] ranges){
        var table = new ArrayList<LocalTime>();
        getAdjustedTimeTableOn(points, ranges, table);
        return table;
    }

    public LocalTime getAdjustedTimeTableOn(List<TimePoint> points, TimeCard.TimeRange[] ranges, List<LocalTime> table){

        if (points != null && !points.isEmpty() && ranges != null && ranges.length > 0){
            Collections.sort(points);

            long startExtra = 0, endExtra = 0;

            for (var range : ranges){

                LocalTime rangeStart = range.start(), rangeEnd = range.end();
                LocalTime expectedStart = rangeStart.plusNanos(endExtra), expectedEnd = rangeEnd.minusNanos(startExtra);
                LocalTime pointStart = getNextTimePoint(points), pointEnd = getNextTimePoint(points);

                long extra;

                if (pointStart == null){
                    pointStart = expectedStart;
                    if (table == null) return pointStart;
                }

                extra = getStartExtra(expectedStart, pointStart);
                startExtra += extra;

                if (pointEnd == null){
                    pointEnd = expectedEnd.minusNanos(extra);
                    if (table == null) return pointEnd;
                }

                extra = getEndExtra(expectedEnd, pointEnd);
                endExtra += extra;

                table.add(pointStart);
                table.add(pointEnd);

            }

        }

        return null;
    }

    public static LocalTime getLateStart(LocalTime expected, long earliness){
        return expected.plus(Duration.ofNanos(earliness));
    }

    public static LocalTime getLateEnd(LocalTime expected, long lateness){
        return expected.plus(Duration.ofNanos(-lateness));
    }

    public static long getStartExtra(LocalTime expected, LocalTime point){
        return point.until(expected, ChronoUnit.NANOS);
    }

    public static long getEndExtra(LocalTime expected, LocalTime point){
        return expected.until(point, ChronoUnit.NANOS);
    }

    private static LocalTime getNextTimePoint(List<TimePoint> points){
        if (!points.isEmpty()){
            var point = points.remove(0);
            return point.time() != null ? point.toTime() : null;
        }
        return null;
    }

}
