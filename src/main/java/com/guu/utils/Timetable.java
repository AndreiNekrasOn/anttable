package com.guu.utils;

import com.guu.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private int numberOfDaysInAWeek;
    private ArrayList<ActivityTimeslot> classes;

    public Timetable(int numberOfDaysInAWeek, ArrayList<ActivityTimeslot> classes) {
        this.numberOfDaysInAWeek = numberOfDaysInAWeek;
        this.classes = classes;
    }


    public int getNumberOfDaysInAWeek() {
        return numberOfDaysInAWeek;
    }

    public void setNumberOfDaysInAWeek(int numberOfDaysInAWeek) {
        this.numberOfDaysInAWeek = numberOfDaysInAWeek;
    }

    public ArrayList<ActivityTimeslot> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<ActivityTimeslot> classes) {
        this.classes = classes;
    }

    public int checkConstraints(List<Constraint> constraints) {
        return constraints.stream()
                .map(c -> c.checkConstraint(this))
                .reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        classes.stream().sorted((tc1, tc2) -> {
            if (tc1.getTimeslot().getWeekday() != tc2.getTimeslot().getWeekday()) {
                return tc1.getTimeslot().getWeekday() - tc2.getTimeslot().getWeekday();
            } else {
                return tc1.getTimeslot().getClassNumber() - tc2.getTimeslot().getClassNumber();
            }
        }).forEach(tc ->  {
            result.append("{");
            result.append(tc);
            result.append("}\n");
        });
        return result.toString();
    }
}
