package com.guu.anttable.utils;

public class NamedEntity {

    protected String name;

    public NamedEntity(String name) {
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
