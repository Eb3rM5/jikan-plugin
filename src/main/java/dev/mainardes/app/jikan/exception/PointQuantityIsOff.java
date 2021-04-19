package dev.mainardes.app.jikan.exception;

public class PointQuantityIsOff extends Exception {

    public PointQuantityIsOff(){
        super("Point quantity is off (range quantity is lower or greater)");
    }

}
