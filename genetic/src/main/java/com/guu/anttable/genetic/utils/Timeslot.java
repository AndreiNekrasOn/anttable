package com.guu.anttable.genetic.utils;

public record Timeslot(int weekday, int classNumber) {
    public Timeslot(int weekday) {
        this(weekday, 0);
    }
}
