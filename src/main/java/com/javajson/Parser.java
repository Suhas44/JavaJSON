package com.javajson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Parser {

    public static void main(String[] args) throws Exception {
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

    public static JSONObject parseField(String data) {
        System.out.println(data);
        String fieldName = data.substring(data.indexOf('\"') + 1, data.indexOf(':') - 1);
        System.out.println(fieldName);
        String typeOfObject = "";
        if (data.charAt(data.indexOf(':') + 1) == '{') {
            typeOfObject = "Object";
        } else if (data.charAt(data.indexOf(':') + 1) == '[') {
            typeOfObject = "Array";
        } else if (data.charAt(data.indexOf(':') + 1) == '\"') {
            typeOfObject = "String";
        } else if (data.substring(data.indexOf(':') + 1, data.indexOf(':') + 6).equals("true,") //test these
                || data.substring(data.indexOf(':') + 1, data.indexOf(':') + 7).equals("false,")) {
            typeOfObject = "Boolean";
        } else if (data.substring(data.indexOf(':') + 1, data.indexOf(':') + 6).equals("null,")) { //test these
            typeOfObject = "Null";
        } else if ((data.charAt(data.indexOf(':') + 1) == '-' && Character.isDigit(data.charAt(data.indexOf(':') + 2))) //test these
                || Character.isDigit(data.charAt(data.indexOf(':') + 1))) {
            typeOfObject = "Number";
        }

        //check edge cases : 127sygxyi7%$^*&(3) would be considered a number, maybe add a check for the next character being a comma, this check has been added for true, false, and null
        System.out.println(typeOfObject);

        return null;
    }

}
