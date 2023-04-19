package com.guu.anttable.web;

import com.guu.anttable.utils.ActivityTimeslot;

/**
 * @author nekrasov
 * Date: 27.12.2022
 */
public class Activity {

    private static final String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String day;
    private final String group;
    private final String subject;
    private final String teacher;
    private final String time;

    public Activity(ActivityTimeslot activity) {
        this.day = weekdays[activity.getTimeslot().getWeekday()];
        this.group = activity.getActivity().getGroup().toString();
        this.subject = activity.getActivity().getSubject().toString();
        this.teacher = activity.getActivity().getTeacher().toString();
        this.time = String.valueOf(activity.getTimeslot().getClassNumber());
    }

    public String getDay() {
        return day;
    }

    public String getGroup() {
        return group;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTime() {
        return time;
    }
}
