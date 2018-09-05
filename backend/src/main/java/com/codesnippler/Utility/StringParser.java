package com.codesnippler.Utility;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringParser {
    public static List<String> parseArrayString(String str) {
        if (!str.startsWith("[") || !str.endsWith("]"))
            return null;

        int length = str.length();
        str = str.substring(1, length - 1);
        String[] strArray = str.split(",");

        return Arrays.asList(strArray);
    }
}
