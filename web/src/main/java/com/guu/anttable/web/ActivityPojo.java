package com.guu.anttable.web;

import com.guu.anttable.utils.ActivityTimeslot;
import com.guu.anttable.utils.DayFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nekrasov
 * Date: 27.12.2022
 */
public class ActivityPojo {

    // TODO: change manual shift to parameter
    private static final DayFormat firstShift = new DayFormat(new ArrayList<>(
            List.of("8:15 - 8:55", "9:05 - 9:50", "10:00 - 11:45",
                    "12:30 - 13:15", "14:00 - 14:45")));
    private static final String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String day;
    private final String group;
    private final String subject;
    private final String teacher;
    private final String time;

    public ActivityPojo(ActivityTimeslot activity) {
        this.day = weekdays[activity.getTimeslot().getWeekday()];
        this.group = activity.getActivity().getGroup().toString();
        this.subject = activity.getActivity().getSubject().toString();
        this.teacher = activity.getActivity().getTeacher().toString();

        this.time = firstShift.getTimes().get(activity.getTimeslot().getClassNumber());
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
