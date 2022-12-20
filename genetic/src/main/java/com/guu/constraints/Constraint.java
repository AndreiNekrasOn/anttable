package com.guu.anttable.constraints;

import com.guu.anttable.utils.Timetable;

public abstract class Constraint {

    Boolean hard;

    public abstract double checkConstraint(Timetable gt); // returns constraint

    public Boolean getHard() {
        return hard;
    }

    protected void setHard(Boolean hard) {
        this.hard = hard;
    }
}
