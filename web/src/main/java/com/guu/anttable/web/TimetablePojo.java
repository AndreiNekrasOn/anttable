package com.guu.anttable.web;

import com.guu.anttable.utils.Timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nekrasov
 * Date: 27.12.2022
 */
public class TimetablePojo {

    private final String institute;
    private final List<ActivityPojo> activities;

    public TimetablePojo(Timetable timetable) {
        this.institute = "ИИС"; // TODO: add field to timetable
        activities = new ArrayList<>();
        timetable.getClasses().forEach(t -> activities.add(new ActivityPojo(t)));
    }

    public String getInstitute() {
        return institute;
    }

    public List<ActivityPojo> getActivities() {
        return activities;
    }
}
