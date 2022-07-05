package com.guu.utils;

public class ActivityTimeslot {
    private final Activity activity;
    private Timeslot timeslot;
    private final String cabinet;

    private final DayFormat format;

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
        return weekdays[timeslot.getWeekday()] + "\n" + activity  + " [" + cabinet + "]\n" +
                format.getTimes().get(timeslot.getClassNumber());
    }

    public Activity getActivity() {
        return activity;
    }
}
