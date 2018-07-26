package com.codesnippler.Model;


import com.codesnippler.Utility.JsonUtility;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


public abstract class JsonModel {
    public JsonObject toJson() {
        Class<?> objClass = this.getClass();
        Field[] fields = objClass.getDeclaredFields();

        JsonObjectBuilder json = Json.createObjectBuilder();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            field.setAccessible(true);
            String key = field.getName();

            try {
                Object value = field.get(this);
                json = JsonUtility.addJsonValue(json, key, value);

            } catch (IllegalAccessException | ClassCastException e) {
                System.out.println("Unable to Serialize Object");
                return null;
            }
        }

        return json.build();
    }


    public JsonObject toJson(Collection<String> hidden) {
        HashSet<String> hiddenKeys = new HashSet<>(hidden);
        Class<?> objClass = this.getClass();
        Field[] fields = objClass.getDeclaredFields();

        JsonObjectBuilder json = Json.createObjectBuilder();

        for (Field field : fields) {
            if (hiddenKeys.contains(field.getName()) || Modifier.isStatic(field.getModifiers()))
                continue;

            field.setAccessible(true);
            String key = field.getName();

            try {
                Object value = field.get(this);
                json = JsonUtility.addJsonValue(json, key, value);

            } catch (IllegalAccessException | ClassCastException e) {
                System.out.println("Unable to Serialize Object");
                return null;
            }
        }

        return json.build();
    }


    public JsonObject toJson(Map<String, ?> addOns) {
        Class<?> objClass = this.getClass();
        Field[] fields = objClass.getDeclaredFields();

        JsonObjectBuilder json = Json.createObjectBuilder();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            field.setAccessible(true);
            String key = field.getName();

            try {
                Object value = field.get(this);
                json = JsonUtility.addJsonValue(json, key, value);

            } catch (IllegalAccessException | ClassCastException e) {
                System.out.println("Unable to Serialize Object");
                return null;
            }
        }

        Iterator itr = addOns.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry pair = (Map.Entry)itr.next();
            String key = (String)pair.getKey();
            Object value = pair.getValue();
            json = JsonUtility.addJsonValue(json, key, value);
        }

        return json.build();
    }

}
