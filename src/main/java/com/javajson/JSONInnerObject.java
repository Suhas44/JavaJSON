package com.javajson;

import java.util.ArrayList;

public class JSONInnerObject extends JSONObject {

    public ArrayList<JSONObject> value;

    public JSONInnerObject(String name, ArrayList<JSONObject> value) {
        super(name);
        this.value = value;
    }

    public ArrayList<JSONObject> getValue() {
        return value;
    }

    @Override
    public String getStringVal() {
        String result = "";
        for (JSONObject obj : value) {
            result += obj.getStringVal();
        }
        return result;
    }

}
