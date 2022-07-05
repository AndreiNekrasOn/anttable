package com.guu.utils;

import com.guu.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private int numberOfDaysInAWeek;
    private List<ActivityTimeslot> classes;
    private double fitnessScore;

    public Timetable(int numberOfDaysInAWeek, List<ActivityTimeslot> classes) {
        this.numberOfDaysInAWeek = numberOfDaysInAWeek;
        this.classes = classes;
    }

    public Timetable(List<ActivityTimeslot> classes) {
        this.numberOfDaysInAWeek = -1;
        this.classes = classes;
    }

    public int getNumberOfDaysInAWeek() {
        return numberOfDaysInAWeek;
    }

    public void setNumberOfDaysInAWeek(int numberOfDaysInAWeek) {
        this.numberOfDaysInAWeek = numberOfDaysInAWeek;
    }

    public List<ActivityTimeslot> getClasses() {
        return classes;
    }

    public void setClasses(List<ActivityTimeslot> classes) {
        this.classes = classes;
    }

    public double checkConstraints(List<Constraint> constraints) {
        return constraints.stream()
                .map(c -> c.checkConstraint(this))
                .reduce(0., Double::sum);
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

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(List<Constraint> constraints) {
        this.fitnessScore = checkConstraints(constraints);
    }
}
