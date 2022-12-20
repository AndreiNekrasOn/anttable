package com.guu.anttable.utils;

import java.util.ArrayList;

public class DayFormat {

    private ArrayList<String> times;

    public DayFormat(ArrayList<String> times) {
        /*
         * Example: times = {"8:15 - 8:55", "9:05 - 9:50"}.
         * odd means start of class, even means end.
         */
        this.times = new ArrayList<>(times);
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return "DayFormat{" +
                "times=" + times +
                '}';
    }
}
