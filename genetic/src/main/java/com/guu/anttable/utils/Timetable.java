package com.guu.anttable.utils;

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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("""
                {"Institute": "ИИС",
                "Расписание": [
                """);
        classes.stream().sorted((tc1, tc2) -> {
            if (tc1.getTimeslot().getWeekday() != tc2.getTimeslot().getWeekday()) {
                return tc1.getTimeslot().getWeekday() -
                        tc2.getTimeslot().getWeekday();
            } else {
                return tc1.getTimeslot().getClassNumber() -
                        tc2.getTimeslot().getClassNumber();
            }
        }).forEach(tc -> {
            result.append(tc);
            result.append(",\n");
        });
        result.deleteCharAt(result.lastIndexOf(","));
        result.append("]\n}");
        return result.toString();
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(double score) {
        this.fitnessScore = score;
    }
}
