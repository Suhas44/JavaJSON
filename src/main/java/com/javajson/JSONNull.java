package com.javajson;

public class JSONNull extends JSONObject {

    Object value = null;

    public JSONNull(String name) {
        super(name);
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String getStringVal() {
        return "null";
    }

}
