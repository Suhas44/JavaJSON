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

    public String extractString(ArrayList<Object> value) {
        StringBuilder sb = new StringBuilder();
        for (Object s : value) {
            if (s instanceof ArrayList) {
                sb.append(extractString((ArrayList<Object>) s));
            } else if (s instanceof JSONArray) {
                sb.append(((JSONArray) s).getStringVal());
            } else if (s instanceof JSONObject) {
                sb.append(((JSONObject) s).getStringVal());
            }
            sb.append("\t");
        }
        return sb.toString();
    }

    @Override
    public String getStringVal() {
        StringBuilder sb = new StringBuilder();
        for (Object s : value) {
            if (s instanceof ArrayList) {
                sb.append(extractString((ArrayList<Object>) s));
            } else if (s instanceof JSONArray) {
                sb.append(((JSONArray) s).getStringVal());
            } else if (s instanceof JSONObject) {
                sb.append(((JSONObject) s).getStringVal());
            } else {
                sb.append(s.toString());
            }
            sb.append("\t");
        }

        return sb.toString();
    }

}
