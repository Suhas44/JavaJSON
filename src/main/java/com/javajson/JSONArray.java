package com.javajson;

import java.util.ArrayList;

public class JSONArray extends JSONObject {

    public ArrayList<Object> value;

    public JSONArray(String name, ArrayList<Object> value) {
        super(name);
        this.value = value;
    }

    public ArrayList<Object> getValue() {
        return value;
    }

    @Override
    public String getStringVal() {
        StringBuilder sb = new StringBuilder();
        for (Object s : value) {
            sb.append(s.toString());
            sb.append("\t");
        }

        return sb.toString();
    }

}
