package com.guu.constraints;

import com.guu.utils.Timetable;

public class Intersections extends Constraint{
    public Intersections(Boolean hard) {
        setHard(true);
    }

    public int checkConstraint(Timetable gt) {
        return 0;
    }
}
