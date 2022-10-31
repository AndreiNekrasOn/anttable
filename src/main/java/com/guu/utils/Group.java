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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((requiredClasses == null) ? 0 : requiredClasses.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (requiredClasses == null) {
            if (other.requiredClasses != null)
                return false;
        } else if (!requiredClasses.equals(other.requiredClasses))
            return false;
        return true;
    }

}
