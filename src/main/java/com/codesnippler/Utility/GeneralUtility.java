package com.codesnippler.Utility;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralUtility {
    public static Set<String> getClassInstanceVars(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Set<String> result = new HashSet<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                result.add(field.getName());
            }
        }
        return result;
    }


    public static Set<String> getClassStaticVars(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Set<String> result = new HashSet<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                result.add(field.getName());
            }
        }
        return result;
    }


    public static Set<String> getAllVars(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Set<String> result = new HashSet<>();
        for (Field field : fields) {
            result.add(field.getName());
        }
        return result;
    }
}
