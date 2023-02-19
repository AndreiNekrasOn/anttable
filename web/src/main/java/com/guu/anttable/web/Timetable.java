package com.guu.anttable.web;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nekrasov
 * Date: 27.12.2022
 */
public class Timetable {

    private final String institute;
    private final List<Activity> activities;

    public Timetable(com.guu.anttable.utils.Timetable timetable) {
        this.institute = "ИИС"; // TODO: add field to timetable
        activities = new ArrayList<>();
        timetable.getClasses().forEach(t -> activities.add(new Activity(t)));
    }

    public String getInstitute() {
        return institute;
    }

    public List<Activity> getActivities() {
        return activities;
    }
}
