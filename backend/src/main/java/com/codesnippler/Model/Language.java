package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestParam;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;


public class Language extends JsonModel {
    @Id
    private String languageId;

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

    public String getId() {
        return languageId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
}
