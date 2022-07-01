package com.guu.constraints;

import com.guu.utils.Timetable;

public abstract class Constraint {
    Boolean hard;
    public abstract int checkConstraint(Timetable gt); // returns constraint
    public Boolean getHard() { return hard; }
    protected void setHard(Boolean hard) { this.hard = hard; }
}
