package com.guu.utils;

public class Teacher {
    private String name;
    public Teacher(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
