package com.guu.utils;

public class Activity {
    private final Group group;
    private final Teacher teacher;
    private final Subject subject;

    public Activity(Group group, Teacher teacher, Subject subject) {
        this.group = group;
        this.teacher = teacher;
        this.subject = subject;
    }

    public Group getGroup() {
        return group;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return group + ": " + subject + " -- " + teacher;
    }
}
