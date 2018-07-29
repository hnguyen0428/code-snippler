package com.codesnippler.Utility;

import com.codesnippler.Model.JsonModel;

import javax.json.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class JsonUtility {
    public static JsonArray listToJson(Iterable<?> list) {
        JsonArrayBuilder result = Json.createArrayBuilder();
        for (Object value : list) {
            try {
                result = addJsonValue(result, value);
            } catch (ClassCastException e) {
                return null;
            }
        }

        return result.build();
    }

    public static JsonObject mapToJson(Map<String, ?> map) {
        Iterator itr = map.entrySet().iterator();
        JsonObjectBuilder result = Json.createObjectBuilder();

        while (itr.hasNext()) {
            Map.Entry pair = (Map.Entry)itr.next();
            String key = (String)pair.getKey();
            Object value = pair.getValue();

            try {
                result = addJsonValue(result, key, value);
            } catch (ClassCastException e) {
                return null;
            }
        }

        return result.build();
    }

    public static JsonObjectBuilder addJsonValue(JsonObjectBuilder json, String key, Object value) throws ClassCastException {
        if (value instanceof String) {
            json = json.add(key, (String)value);
        }
        else if (value instanceof Byte || value instanceof Short
                || value instanceof Integer || value instanceof Long) {
            json = json.add(key, (Long)value);
        }
        else if (value instanceof Float || value instanceof Double) {
            json = json.add(key, (Double)value);
        }
        else if (value instanceof Map) {
            json = json.add(key, JsonUtility.mapToJson((Map)value));
        }
        else if (value instanceof List) {
            json = json.add(key, JsonUtility.listToJson((List)value));
        }
        else if (value instanceof JsonModel) {
            json = json.add(key, ((JsonModel)value).toJson());
        }
        else if (value instanceof JsonValue) {
            json = json.add(key, (JsonValue)value);
        }
        else if (value != null) {
            json = json.add(key, value.toString());
        }
        return json;
    }

    public static JsonObjectBuilder addJsonValues(JsonObjectBuilder json, Map<String, ?> map) {
        Iterator itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry pair = (Map.Entry)itr.next();
            json = addJsonValue(json, (String)(pair.getKey()), pair.getValue());
        }
        return json;
    }


    public static JsonArrayBuilder addJsonValue(JsonArrayBuilder json, Object value) throws ClassCastException {
        if (value instanceof String) {
            json = json.add((String)value);
        }
        else if (value instanceof Byte || value instanceof Short
                || value instanceof Integer || value instanceof Long) {
            json = json.add((Long)value);
        }
        else if (value instanceof Float || value instanceof Double) {
            json = json.add((Double)value);
        }
        else if (value instanceof Map) {
            json = json.add(JsonUtility.mapToJson((Map)value));
        }
        else if (value instanceof List) {
            json = json.add(JsonUtility.listToJson((List)value));
        }
        else if (value instanceof JsonModel) {
            json = json.add(((JsonModel)value).toJson());
        }
        else if (value instanceof JsonValue) {
            json = json.add((JsonValue)value);
        }
        else if (value != null) {
            json = json.add(value.toString());
        }
        return json;
    }

}
