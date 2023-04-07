package com.javajson;

public class JSONNumber extends JSONObject {

    public double value;

    public JSONNumber(String name, double value) {
        super(name);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getStringVal() {
        return String.valueOf(value);
    }

}
