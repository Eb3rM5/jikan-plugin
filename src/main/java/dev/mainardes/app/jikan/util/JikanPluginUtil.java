package dev.mainardes.app.jikan.util;

import dev.mainardes.app.jikan.entity.TimePoint;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class JikanPluginUtil {

    public static String getLocalTimeString(long nanoOfDay){
        var time = LocalTime.ofNanoOfDay(Math.abs(nanoOfDay));
        return (nanoOfDay < 0 ? "-" : "") + time;
    }

    public static boolean isBetween(TimePoint point, LocalDateTime start, LocalDateTime end){
        if (point == null) return false;
        var date = point.time();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public static boolean isBetween(LocalTime time, LocalTime start, LocalTime end){
        return !time.isBefore(start) && !time.isAfter(end);
    }

    public static void delete(Path path) {
        try {
            if (Files.isDirectory(path)){
                Files.list(path).forEach(JikanPluginUtil::delete);
            }

            Files.deleteIfExists(path);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Type[] getGenericTypesOf(Class<?> classType){
        Type genericSuperclass = classType.getGenericSuperclass();
        return (genericSuperclass instanceof ParameterizedType type)
                ? type.getActualTypeArguments()
                : null;
    }

    public static Type getGenericTypeOf(Class<?> classType){
        var types = getGenericTypesOf(classType);
        return types != null && types.length > 0
                ? types[0]
                : null;
    }

    public static String getCaller(Class<?> typeToFind) {
        try {
            throw new Exception();
        } catch (Exception fake){
            var trace = fake.getStackTrace();
            for (var element : trace){
                try {
                    String name = element.getClassName();
                    if (typeToFind.isAssignableFrom(Class.forName(name))){
                        return name;
                    }
                } catch (ClassNotFoundException ignored){
                }
            }

            return null;
        }
    }

    public static <T> T newInstanceOf(Class<T> type){
        try {
            var constructor = type.getDeclaredConstructor();
            if (force && !constructor.canAccess(null)){
                constructor.setAccessible(true);
            }

            return constructor.newInstance();
        } catch (ReflectiveOperationException e){
            e.printStackTrace();
        }

        return null;
    }

    private JikanPluginUtil() {}

}
