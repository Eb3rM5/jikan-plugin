package dev.mainardes.app.jikan.plugin;

import dev.mainardes.app.jikan.exception.PluginManagerAlreadyRegistered;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

public final class WatchTimePluginTestCase {

    @Test
    public void getEarlyEnd(){
        LocalTime expectedStart = LocalTime.of(8, 0);
        LocalTime startPoint = LocalTime.of(7, 58);

        long startExtra = WatchTimePlugin.getStartExtra(expectedStart, startPoint);

        LocalTime expectedEnd = LocalTime.of(18, 18);
        LocalTime point = WatchTimePlugin.getLateEnd(expectedEnd, startExtra);

        assert point.toString().equals("18:16") : "Wrong start";
    }

    @Test
    public void getLateEnd(){
        LocalTime expectedStart = LocalTime.of(8, 0);
        LocalTime startPoint = LocalTime.of(8, 2);

        long startExtra = WatchTimePlugin.getStartExtra(expectedStart, startPoint);

        LocalTime expectedEnd = LocalTime.of(18, 18);
        LocalTime point = WatchTimePlugin.getLateEnd(expectedEnd, startExtra);

        assert point.toString().equals("18:20") : "Wrong start";
    }

    @Test
    public void getLateStart(){
        LocalTime expectedEnd = LocalTime.of(12, 0);
        LocalTime endPoint = LocalTime.of(12, 2);

        long endExtra = WatchTimePlugin.getEndExtra(expectedEnd, endPoint);

        LocalTime expectedStart = LocalTime.of(13, 30);
        LocalTime point = WatchTimePlugin.getLateStart(expectedStart, endExtra);

        assert point.toString().equals("13:32") : "Wrong start";
    }

    @Test
    public void getEarlyStart(){
        LocalTime expectedEnd = LocalTime.of(12, 0);
        LocalTime endPoint = LocalTime.of(11, 58);

        long endExtra = WatchTimePlugin.getEndExtra(expectedEnd, endPoint);

        LocalTime expectedStart = LocalTime.of(13, 30);
        LocalTime point = WatchTimePlugin.getLateStart(expectedStart, endExtra);

        assert point.toString().equals("13:28") : "Wrong start";
    }


    @Test
    public void getStartExtra(){

        LocalTime expected = LocalTime.of(8, 0);
        LocalTime point = LocalTime.of(7, 58);

        long extra = WatchTimePlugin.getStartExtra(expected, point);
        assert extra == Duration.ofMinutes(2).toNanos() : "Start extra value is being wrongly calculated: " + extra;
    }

    @Test
    public void getLateStartExtra(){

        LocalTime expected = LocalTime.of(8, 0);
        LocalTime point = LocalTime.of(8, 2);

        long extra = WatchTimePlugin.getStartExtra(expected, point);
        assert extra == -Duration.ofMinutes(2).toNanos() : "Late start extra value is being wrongly calculated: " + extra;
    }

    @Test
    public void getEndExtra(){
        LocalTime expected = LocalTime.of(12, 0);
        LocalTime point = LocalTime.of(12, 2);

        long extra = WatchTimePlugin.getEndExtra(expected, point);
        assert extra == Duration.ofMinutes(2).toNanos() : "End extra value is being wrongly calculated: " + extra;
    }

    @Test
    public void getEarlyEndExtra(){
        LocalTime expected = LocalTime.of(12, 0);
        LocalTime point = LocalTime.of(11, 58);

        long extra = WatchTimePlugin.getEndExtra(expected, point);
        assert extra == -Duration.ofMinutes(2).toNanos() : "Early end extra value is being wrongly calculated: " + extra;
    }

}
