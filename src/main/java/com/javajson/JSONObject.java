package com.javajson;

public abstract class JSONObject {
    public String name;

    public JSONObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getStringVal();
}
