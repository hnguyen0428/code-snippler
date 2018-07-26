package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;


public class Language extends JsonModel {
    @Id
    private String id;

    private String name;
    private String type;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public Language() {}

    public Language(String name, String type, Date date) {
        this.name = name;
        this.type = type;
        this.createdDate = date;
    }

    @Override
    public String toString() {
        return String.format(
                "Name[id=%s, name='%s', type='%s']",
                id, name, type);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("_id", id)
                .add("name", name)
                .add("type", type)
                .add("createdDate", createdDate.toString())
                .build();
    }

    @Override
    public JsonObject toJson(Collection<String> hidden) {
        HashSet<String> keys = new HashSet<>(hidden);
        JsonObjectBuilder result = Json.createObjectBuilder();

        if (!keys.contains("id")) {
            result.add("_id", id);
        }
        if (!keys.contains("name")) {
            result.add("name", name);
        }
        if (!keys.contains("type")) {
            result.add("type", type);
        }
        if (!keys.contains("createdDate")) {
            result.add("createdDate", createdDate.toString());
        }

        return result.build();
    }
}
