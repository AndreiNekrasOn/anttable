package com.guu.utils;

public class Timeslot {
    private final int weekday;
    private final int classNumber;

    public Timeslot(int weekday, int classNumber) {
        this.weekday = weekday;
        this.classNumber = classNumber;
    }

    public Timeslot(int weekday) {
        this.weekday = weekday;
        this.classNumber = 0;
    }

    public int getWeekday() {
        return weekday;
    }

    public int getClassNumber() {
        return classNumber;
    }
}
