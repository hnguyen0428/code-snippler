package com.codesnippler.Model;


import com.codesnippler.Utility.JsonUtility;
import org.springframework.data.annotation.Transient;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


public abstract class JsonModel {
    public JsonObject toJson() {
        return toJsonBuilder().build();
    }


    public JsonObjectBuilder toJsonBuilder() {
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

        return json;
    }


    public JsonObjectBuilder toJsonBuilder(Collection<String> hidden) {
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

        return json;
    }


    public JsonObject toJson(Collection<String> hidden) {
        return toJsonBuilder(hidden).build();
    }


    public JsonObject toJson(Map<String, ?> addOns) {
        return toJsonBuilder(addOns).build();
    }


    public JsonObjectBuilder toJsonBuilder(Map<String, ?> addOns) {
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

        return json;
    }

    public static Set<String> getModelAttributes(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Set<String> result = new HashSet<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && field.getAnnotation(Transient.class) == null) {
                result.add(field.getName());
            }
        }
        return result;
    }

}
