package com.guu.anttable.utils;

public record Timeslot(int weekday, int classNumber) {
    public Timeslot(int weekday) {
        this(weekday, 0);
    }
}
