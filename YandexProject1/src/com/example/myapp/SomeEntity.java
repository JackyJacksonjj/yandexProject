package com.example.myapp;

import java.io.Serializable;

public class SomeEntity implements Serializable {
    private String name = "";
    private double value = 0;
    private String path = "";

    public SomeEntity(String name, double value, String path) {
        this.setName(name);
        this.setValue(value);
        this.setPath(path);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public double getValue() {
        return this.value;
    }

    public String getPath() {
        return this.path;
    }
}
