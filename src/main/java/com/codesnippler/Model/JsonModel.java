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

    private Map<String, Object> addOns = new HashMap<>();

    @Transient
    private boolean valid;

    @Override
    public String toString() {
        return toJson().toString();
    }

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

        json = JsonUtility.addJsonValues(json, this.addOns);
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

        json = JsonUtility.addJsonValues(json, this.addOns);
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

        json = JsonUtility.addJsonValues(json, this.addOns);
        json = JsonUtility.addJsonValues(json, addOns);
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


    public void copyTo(JsonModel model) {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            field.setAccessible(true);

            try {
                field.set(model, field.get(this));

            } catch (IllegalAccessException | ClassCastException e) {
                System.out.println("Unable to retrieve fields");
            }
        }
    }


    public void setValid() {
        valid = true;
    }

    public Map<String, Object> getAddOns() {
        return addOns;
    }

    public void includeInJson(String key, Object value) {
        addOns.put(key, value);
    }

}
