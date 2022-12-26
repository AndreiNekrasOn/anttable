package com.guu.anttable.utils;

public class ActivityTimeslot {

    private final Activity activity;
    private final String cabinet;
    private final DayFormat format;
    private Timeslot timeslot;

    public ActivityTimeslot(Activity activity, Timeslot timeslot,
                            String cabinet, DayFormat format) {
        this.activity = activity;
        this.timeslot = timeslot;
        this.cabinet = cabinet;
        this.format = format;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot ts) {
        this.timeslot = ts;
    }

    public String getCabinet() {
        return cabinet;
    }

    public DayFormat getFormat() {
        return format;
    }

    @Override
    public String toString() {
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return String.format("""
                        {"Day": "%s",
                        "Group": "%s",
                        "Subject": "%s",
                        "Teacher": "%s",
                        "Time": "%s"}
                        """,
                weekdays[timeslot.getWeekday()],
                activity.getGroup(),
                activity.getSubject(),
                activity.getTeacher(),
                format.getTimes().get(timeslot.getClassNumber())
        );
    }

    public Activity getActivity() {
        return activity;
    }
}
