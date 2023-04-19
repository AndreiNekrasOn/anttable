package com.guu.anttable.utils;

import java.util.ArrayList;

public class Group extends NamedEntity {

    private ArrayList<SubjectTeacherPair> requiredClasses;

    public Group(String name, ArrayList<SubjectTeacherPair> requiredClasses) {
        super(name);
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
}
