package com.javajson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Parser {

    public static void main(String[] args) throws Exception { //delete main on completion
        parseFile(System.getProperty("user.dir") + "//src//main//java//com//javajson//" + "make.json");
    }

    public static ArrayList<JSONObject> parseJSON(String data) {
        ArrayList<JSONObject> tree = new ArrayList<JSONObject>();
        data = trim(data);
        // while (data.length() > 0) {
        JSONObject obj = parseField(data);
        tree.add(obj);
        // data = data.substring(data.indexOf(',') + 1); //check this
        // }
        return tree;
    }

    public static ArrayList<JSONObject> parseFile(String filePath) throws Exception { //make this accept a File instead of a String
        String data = new String(Files.readAllBytes(Paths.get(filePath)));
        return parseJSON(data);
    }

    public static String trim(String data) {
        data = data.replaceAll("\\s", "");
        if (data.charAt(0) != '{' || data.charAt(data.length() - 1) != '}')
            throw new Error("Invalid JSON");
        return data.substring(1, data.length() - 1);
    }

    public static Object[] parseField(String data) {
        Object[] o = new Object[2];
        String fieldName = data.substring(data.indexOf('\"') + 1, data.indexOf(':') - 1);
        String typeOfObject = "";
        int startIndex = data.indexOf(':') + 1;
        if (data.charAt(startIndex) == '{') {
            typeOfObject = "Object";
            Object[] toRet = parseObject(data, fieldName);
            // return parseObject(data, fieldName);
        } else if (data.charAt(startIndex) == '[') {
            typeOfObject = "Array";
            Object[] toRet = parseArray(data, fieldName);
            // return parseArray(data, fieldName);
        } else if (data.charAt(startIndex) == '\"') {
            typeOfObject = "String";
            return parseString(data, fieldName);
            // return parseString(data, fieldName);
        } else if (data.substring(startIndex, startIndex + 5).equals("true,")
                || data.substring(startIndex, startIndex + 6).equals("false,")) {
            typeOfObject = "Boolean";
            Object[] toRet = parseBoolean(data, fieldName);
            // return parseBoolean(data, fieldName);
        } else if (data.substring(startIndex, startIndex + 5).equals("null,")) {
            typeOfObject = "Null";
            Object[] toRet = parseNull(data, fieldName);
            // return parseNull(data, fieldName);
        } else if ((data.charAt(startIndex) == '-' && Character.isDigit(data.charAt(startIndex + 1)))
                || Character.isDigit(data.charAt(startIndex))) {
            typeOfObject = "Number";
            Object[] toRet = parseNumber(data, fieldName);
            // return parseNumber(data, fieldName);
        }

        if (typeOfObject.equals("")) {
            throw new Error("Invalid JSON at field: " + fieldName);
        }

        return null;
    }

    public static Object[] parseString(String data, String fieldName) {
        Object[] o = new Object[2];
        for (int i = data.indexOf(':') + 2; i < data.length(); i++) {
            if (data.charAt(i) == '\"' && data.charAt(i - 1) != '\\') {
                String string = data.substring(data.indexOf(':') + 2, i);
                o[0] = new JSONString(fieldName, string);
                o[1] = data.indexOf(",", i);
                return o;
            }
        }
        throw new Error("Invalid JSON at field: " + fieldName);
    }

    public static JSONNumber parseNumber(String data, String fieldName) {
        String number = data.substring(data.indexOf(':') + 1, data.indexOf(',', data.indexOf(':') + 1));
        try {
            return new JSONNumber(fieldName, Double.parseDouble(number));
        } catch (Exception e) {
            throw new Error("Invalid JSON at field: " + fieldName);
        }
    }

    public static JSONBoolean parseBoolean(String data, String fieldName) {
        Boolean value = Boolean
                .parseBoolean(data.substring(data.indexOf(':') + 1, data.indexOf(',', data.indexOf(':') + 1)));
        return new JSONBoolean(fieldName, value);
    }

    public static JSONNull parseNull(String data, String fieldName) {
        return new JSONNull(fieldName);
    }

    public static JSONInnerObject parseObject(String data, String fieldName) {
        data = data.substring(data.indexOf(':') + 1, data.indexOf('}', data.indexOf(':')) + 1);
        try {
            return new JSONInnerObject(fieldName, parseJSON(data));
        } catch (Exception e) {
            throw new Error("Invalid JSON at field: " + fieldName);
        }
    }

    public static Object[] parseArrayVal(String data) {
        Object[] o = new Object[2];
        String cols = ":";
        data = cols + data;
        if (data.charAt(1) == '{') {
            int lastIndex = data.indexOf(",", data.indexOf('}'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            o[0] = parseObject(data, "").getValue();
            o[1] = rest;
            return o;
        } else if (data.charAt(1) == '[') {
            int lastIndex = data.indexOf(",", data.indexOf(']'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            o[0] = parseArray(data, "").getValue();
            o[1] = rest;
            return o;
        } else if (data.charAt(1) == '\"') {
            Object[] toRet = parseString(data, "");
            o[0] = ((JSONString) toRet[0]).getValue();
            String rest = ((int) toRet[1]) == -1 ? "" : data.substring((int) toRet[1] + 1);
            o[1] = rest;
            return o;
        } else if (data.substring(1, 6).equals("true,")
                || data.substring(1, 7).equals("false,")) {
            int lastIndex = data.indexOf(',', data.indexOf(':') + 1);
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            o[0] = parseBoolean(data, "").getValue();
            o[1] = rest;
            return o;
        } else if (data.substring(1, 6).equals("null,")) {
            int lastIndex = data.indexOf(',', data.indexOf(':') + 1);
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            o[0] = parseNull(data, "").getValue();
            o[1] = rest;
            return o;
        } else if ((data.charAt(1) == '-' && Character.isDigit(data.charAt(2)))
                || Character.isDigit(data.charAt(1))) {
            int lastIndex = data.indexOf(',', data.indexOf(':') + 1);
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            o[0] = parseNumber(data, "").getValue();
            o[1] = rest;
            return o;
        }
        throw new Error("Invalid JSON");
    }

    public static JSONArray parseArray(String data, String fieldName) {
        ArrayList<Object> array = new ArrayList<Object>();
        int totalOpenings = 1;
        int totalClosings = 0;
        int i = data.indexOf(':') + 2;
        while (totalOpenings != totalClosings) {
            if (data.charAt(i) == '[') {
                totalOpenings++;
            } else if (data.charAt(i) == ']') {
                totalClosings++;
            }
            i++;
        }
        data = data.substring(data.indexOf(':') + 2, i - 1);

        while (data.length() > 0) {
            Object[] arr = parseArrayVal(data);
            Object val = arr[0];
            array.add(val);
            data = (String) arr[1];
            if (data == null) {
                JSONArray n = new JSONArray(fieldName, array);
                System.out.println(n.getValue());
                return n;
            }
        }

        JSONArray n = new JSONArray(fieldName, array);

        System.out.println(n.getValue());
        return new JSONArray(fieldName, array);
    }
}