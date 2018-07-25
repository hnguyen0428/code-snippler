package com.codesnippler.Utility;

import javax.json.Json;
import javax.json.JsonObject;

public class ResponseBuilder {
    public static JsonObject createErrorObject(String message, String type) {
        return Json.createObjectBuilder()
                .add("type", type)
                .add("message", message)
                .build();
    }

    public static JsonObject createErrorResponse(JsonObject error) {
        return Json.createObjectBuilder()
                .add("data", "null")
                .add("success", false)
                .add("error", error)
                .build();
    }

    public static JsonObject createDataResponse(JsonObject data) {
        return Json.createObjectBuilder()
                .add("data", data)
                .add("success", true)
                .build();
    }
}
