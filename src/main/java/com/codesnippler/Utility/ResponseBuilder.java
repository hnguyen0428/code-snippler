package com.codesnippler.Utility;

import javax.json.Json;
import javax.json.JsonArray;
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
                .add("success", false)
                .add("error", error)
                .build();
    }

    public static JsonObject createErrorResponse(String message, String type) {
        JsonObject error = createErrorObject(message, type);
        return Json.createObjectBuilder()
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

    public static JsonObject createDataResponse(JsonArray data) {
        return Json.createObjectBuilder()
                .add("data", data)
                .add("success", true)
                .build();
    }

    public static JsonObject createSuccessResponse() {
        return Json.createObjectBuilder()
                .add("success", true)
                .build();
    }
}
