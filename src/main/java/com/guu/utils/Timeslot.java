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

    @Override 
    public int hashCode() {
        return new Integer(weekday).hashCode() * 31 + new Integer(classNumber).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Timeslot) {
            Timeslot t = (Timeslot) obj;
            return t.weekday == weekday && t.classNumber == classNumber;
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + weekday + "-" + classNumber;
    }
    
}
