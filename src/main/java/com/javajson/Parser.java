package com.javajson;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class Parser {

    public static ArrayList<JSONObject> parseJSON(String data) {
        ArrayList<JSONObject> tree = new ArrayList<JSONObject>();
        data = trim(data);
        while (data.length() > 0) {
            Object[] toRet = parseField(data);
            tree.add((JSONObject) toRet[0]);
            if ((int) toRet[1] == -1) {
                data = "";
                break;
            }
            data = data.substring((int) toRet[1] + 1, data.length());
        }
        return tree;
    }

    public static ArrayList<JSONObject> parseFile(File file) throws Exception {
        String data = new String(Files.readAllBytes(file.toPath()));
        return parseJSON(data);
    }

    public static String trim(String data) {
        data = data.replaceAll("\\s", "");
        if (data.charAt(0) != '{' || data.charAt(data.length() - 1) != '}')
            throw new Error("Invalid JSON");
        return data.substring(1, data.length() - 1);
    }

    public static Object[] parseField(String data) {
        String fieldName = data.substring(data.indexOf('\"') + 1, data.indexOf(':') - 1);
        String typeOfObject = "";
        int startIndex = data.indexOf(':') + 1;
        if (data.charAt(startIndex) == '{') {
            typeOfObject = "Object";
            Object[] toRet = parseObject(data, fieldName);
            return toRet;
        } else if (data.charAt(startIndex) == '[') {
            typeOfObject = "Array";
            Object[] toRet = parseArray(data, fieldName);
            return toRet;
        } else if (data.charAt(startIndex) == '\"') {
            typeOfObject = "String";
            Object[] toRet = parseString(data, fieldName);
            return toRet;
        } else if ((data.charAt(startIndex) == '-' && Character.isDigit(data.charAt(startIndex + 1)))
                || Character.isDigit(data.charAt(startIndex))) {
            typeOfObject = "Number";
            Object[] toRet = parseNumber(data, fieldName);
            return toRet;
        } else if (data.substring(startIndex, startIndex + 5).equals("null,")) {
            typeOfObject = "Null";
            Object[] toRet = parseNull(data, fieldName);
            return toRet;
        } else if (data.substring(startIndex, startIndex + 5).equals("true,")
                || data.substring(startIndex, startIndex + 6).equals("false,")) {
            typeOfObject = "Boolean";
            Object[] toRet = parseBoolean(data, fieldName);
            return toRet;
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

    public static Object[] parseNumber(String data, String fieldName) {
        int indexOfComma = data.indexOf(',', data.indexOf(':') + 1);
        String number = (indexOfComma == -1) ? data.substring(data.indexOf(':') + 1, data.length())
                : data.substring(data.indexOf(':') + 1, indexOfComma);
        try {
            Object[] o = new Object[2];
            o[0] = new JSONNumber(fieldName, Double.parseDouble(number));
            o[1] = data.indexOf(",", data.indexOf(':'));
            return o;
        } catch (Exception e) {
            throw new Error("Invalid JSON at field: " + fieldName);
        }
    }

    public static Object[] parseBoolean(String data, String fieldName) {
        Object[] o = new Object[2];
        int indexOfComma = data.indexOf(',', data.indexOf(':') + 1);
        Boolean value = (indexOfComma == -1)
                ? Boolean.parseBoolean(data.substring(data.indexOf(':') + 1, data.length()))
                : Boolean.parseBoolean(data.substring(data.indexOf(':') + 1, indexOfComma));
        o[0] = new JSONBoolean(fieldName, value);
        o[1] = data.indexOf(",", data.indexOf(':'));
        return o;
    }

    public static Object[] parseNull(String data, String fieldName) {
        Object[] o = new Object[2];
        o[0] = new JSONNull(fieldName);
        o[1] = data.indexOf(",", data.indexOf(':'));
        return o;
    }

    public static Object[] parseObject(String data, String fieldName) {
        data = data.substring(data.indexOf(':') + 1, data.indexOf('}', data.indexOf(':')) + 1);
        try {
            Object[] o = new Object[2];
            o[0] = new JSONInnerObject(fieldName, parseJSON(data));
            o[1] = data.indexOf(",", data.indexOf('}'));
            return o;
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
            Object[] toRet = parseObject(data, "");
            o[0] = ((JSONInnerObject) toRet[0]).getValue();
            o[1] = rest;
            return o;
        } else if (data.charAt(1) == '[') {
            int lastIndex = data.indexOf(",", data.indexOf(']'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            Object[] toRet = parseArray(data, "");
            o[0] = ((JSONArray) toRet[0]).getValue();
            o[1] = rest;
            return o;
        } else if (data.charAt(1) == '\"') {
            int lastIndex = data.indexOf(",", data.indexOf('\"'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            Object[] toRet = parseString(data, "");
            o[0] = ((JSONString) toRet[0]).getValue();
            o[1] = rest;
            return o;
        } else if ((data.charAt(1) == '-' && Character.isDigit(data.charAt(2)))
                || Character.isDigit(data.charAt(1))) {
            int lastIndex = data.indexOf(",", data.indexOf('\"'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            Object[] toRet = parseNumber(data, "");
            o[0] = ((JSONNumber) toRet[0]).getValue();
            o[1] = rest;
            return o;
        } else if (data.substring(1, 6).equals("null,")) {
            int lastIndex = data.indexOf(",", data.indexOf('\"'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            o[0] = null;
            o[1] = rest;
            return o;
        } else if (data.substring(1, 6).equals("true,")
                || data.substring(1, 7).equals("false,")) {
            int lastIndex = data.indexOf(",", data.indexOf('\"'));
            String rest = lastIndex == -1 ? "" : data.substring(lastIndex + 1);
            Object[] toRet = parseBoolean(data, "");
            o[0] = ((JSONBoolean) toRet[0]).getValue();
            o[1] = rest;
            return o;
        }
        throw new Error("Invalid JSON");
    }

    public static Object[] parseArray(String data, String fieldName) {
        ArrayList<Object> array = new ArrayList<Object>();
        Object[] o = new Object[2];
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
            if (data == null || data.length() == 0) {
                o[0] = new JSONArray(fieldName, array);
                o[1] = i - 1;
                return o;
            }
        }
        o[0] = new JSONArray(fieldName, array);
        o[1] = i - 1;
        return o;
    }
}