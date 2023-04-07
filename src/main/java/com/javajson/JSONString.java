package com.javajson;

public class JSONString extends JSONObject {

    public String value;

    public JSONString(String name, String value) {
        super(name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getStringVal() {
        return getValue();
    }

}
