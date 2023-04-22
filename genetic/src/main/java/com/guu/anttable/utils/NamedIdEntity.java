package com.guu.anttable.utils;

public class NamedIdEntity {

    protected String name;
    
    protected int id;

    public NamedIdEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
