package com.guu.utils;

public class SubjectTeacherPair {
    private final Teacher teacher;
    private final Subject subject;

    public Teacher getTeacher() {
        return teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public SubjectTeacherPair(Subject subject, Teacher teacher) {
        this.teacher = teacher;
        this.subject = subject;
    }

    @Override
    public String toString() {
        return subject + " -- " + teacher;
    }
}
