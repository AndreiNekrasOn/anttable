package com.guu.anttable.utils;

public class ActivityTimeslot {

    private final Activity activity;
    private final String cabinet;
    private Timeslot timeslot;

    public ActivityTimeslot(Activity activity, Timeslot timeslot, String cabinet) {
        this.activity = activity;
        this.timeslot = timeslot;
        this.cabinet = cabinet;
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

    public Activity getActivity() {
        return activity;
    }    @Override

    public String toString() {
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
            "Mon2", "Tue2", "Wed2", "Thu2", "Fri2", "Sat2"};
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
                timeslot.getClassNumber()
        );
    }

}
