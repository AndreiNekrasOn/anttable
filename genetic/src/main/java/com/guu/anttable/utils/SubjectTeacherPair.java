package com.guu.anttable.utils;

public class SubjectTeacherPair {

    private final Teacher teacher;
    private final Subject subject;

    public SubjectTeacherPair(Subject subject, Teacher teacher) {
        this.teacher = teacher;
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Subject getSubject() {
        return subject;
    }
}
