package com.guu.anttable.constraints;

import com.guu.anttable.utils.Timetable;

public abstract class Constraint {

    /**
     * Признак обязательности выполнения ограничения
     */
    Boolean hard;

    /**
     *
     * @param gt - расписание
     * @return значение целевой функции
     */
    public abstract double checkConstraint(Timetable gt);

    public Boolean getHard() {
        return hard;
    }

    protected void setHard(Boolean hard) {
        this.hard = hard;
    }
}
