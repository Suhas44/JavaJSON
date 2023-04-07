package com.javajson;

public class JSONBoolean extends JSONObject {

    public boolean value;

    public JSONBoolean(String name, boolean value) {
        super(name);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String getStringVal() {
        return String.valueOf(value);
    }

}
