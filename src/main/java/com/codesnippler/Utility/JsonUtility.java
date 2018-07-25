package com.codesnippler.Utility;

import com.codesnippler.Model.JsonModel;

import javax.json.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtility {
    public static JsonObject stringMapToJson(Map<String, String> map) {
        Set<String> keys = map.keySet();
        JsonObjectBuilder result = Json.createObjectBuilder();

        for (String key : keys) {
            result = result.add(key, map.get(key));
        }

        return result.build();
    }

    public static JsonObject numberMapToJson(Map<String, ? extends Number> map) {
        Set<String> keys = map.keySet();
        JsonObjectBuilder result = Json.createObjectBuilder();

        for (String key : keys) {
            switch (map.get(key).getClass().getName()) {
                case "Byte":
                case "Short":
                case "Integer":
                case "Long":
                case "AtomicInteger":
                case "AtomicLong":
                    result = result.add(key, map.get(key).longValue());
                    break;
                case "Float":
                case "Double":
                    result = result.add(key, map.get(key).doubleValue());
                    break;
            }
        }

        return result.build();
    }

    public static JsonObject booleanMapToJson(Map<String, Boolean> map) {
        Set<String> keys = map.keySet();
        JsonObjectBuilder result = Json.createObjectBuilder();

        for (String key : keys) {
            result = result.add(key, map.get(key));
        }

        return result.build();
    }

    public static JsonObject modelMapToJson(Map<String, ? extends JsonModel> map) {
        Set<String> keys = map.keySet();
        JsonObjectBuilder result = Json.createObjectBuilder();

        for (String key : keys) {
            result = result.add(key, map.get(key).toJson());
        }

        return result.build();
    }

    public static JsonArray modelListToJson(List<JsonModel> list) {
        JsonArrayBuilder result = Json.createArrayBuilder();
        for (JsonModel model : list) {
            result = result.add(model.toJson());
        }

        return result.build();
    }

    public static JsonArray modelListToJson(List<JsonModel> list, List<String> hidden) {
        JsonArrayBuilder result = Json.createArrayBuilder();
        for (JsonModel model : list) {
            result = result.add(model.toJson(hidden));
        }

        return result.build();
    }

}
