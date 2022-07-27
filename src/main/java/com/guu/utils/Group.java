package com.guu.utils;

import java.util.ArrayList;

public class Group {
    private ArrayList<SubjectTeacherPair> requiredClasses;
    private final String name;

    public Group(String name, ArrayList<SubjectTeacherPair> requiredClasses) {
        this.name = name;
        this.requiredClasses = requiredClasses;
    }

    public String getName() {
        return name;
    }
    public void addClass(SubjectTeacherPair sbt) {
        requiredClasses.add(sbt);
    }

    public ArrayList<SubjectTeacherPair> getRequiredClasses() {
        return requiredClasses;
    }

    @Override
    public String toString() {
        return name;
    }
}
