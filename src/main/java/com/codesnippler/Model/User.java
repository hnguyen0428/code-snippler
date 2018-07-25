package com.codesnippler.Model;

import com.codesnippler.Utility.JsonUtility;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;


public class User extends JsonModel {
    @Id
    private String id;

    private String username;
    private String password;
    private String apiKey;
    private HashMap<String, Boolean> savedSnippets;
    private HashMap<String, Boolean> createdSnippets;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public User() {}

    public User(String username, String password, String apiKey, HashMap<String, Boolean> savedSnippets,
                HashMap<String, Boolean> createdSnippets, Date createdDate) {
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
        this.savedSnippets = savedSnippets;
        this.createdSnippets = createdSnippets;
        this.createdDate = createdDate;
    }

    public User(String username, String password, String apiKey, Date createdDate) {
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
        this.createdDate = createdDate;

        this.savedSnippets = new HashMap<>();
        this.createdSnippets = new HashMap<>();
    }

    @Override
    public String toString() {
        return String.format("{ \"username\": \"%s\", \"apiKey\": \"%s\", \"createdDate\": \"%s\" }", username, apiKey, createdDate);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public HashMap<String, Boolean> getSavedSnippets() {
        return savedSnippets;
    }

    public void setSavedSnippets(HashMap<String, Boolean> savedSnippets) {
        this.savedSnippets = savedSnippets;
    }

    public HashMap<String, Boolean> getCreatedSnippets() {
        return createdSnippets;
    }

    public void setCreatedSnippets(HashMap<String, Boolean> createdSnippets) {
        this.createdSnippets = createdSnippets;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }


    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("_id", id)
                .add("username", username)
                .add("password", password)
                .add("apiKey", apiKey)
                .add("createdDate", createdDate.toString())
                .add("savedSnippets", JsonUtility.booleanMapToJson(savedSnippets))
                .add("createdSnippets", JsonUtility.booleanMapToJson(createdSnippets))
                .build();
    }

    @Override
    public JsonObject toJson(Collection<String> hidden) {
        HashSet<String> keys = new HashSet< >(hidden);
        JsonObjectBuilder result = Json.createObjectBuilder();

        if (!keys.contains("id")) {
            result.add("_id", id);
        }
        if (!keys.contains("username")) {
            result.add("username", username);
        }
        if (!keys.contains("password")) {
            result.add("password", password);
        }
        if (!keys.contains("apiKey")) {
            result.add("apiKey", apiKey);
        }
        if (!keys.contains("createdDate")) {
            result.add("createdDate", createdDate.toString());
        }
        if (!keys.contains("savedSnippets")) {
            result.add("savedSnippets", JsonUtility.booleanMapToJson(savedSnippets));
        }
        if (!keys.contains("createdSnippets")) {
            result.add("createdSnippets", JsonUtility.booleanMapToJson(createdSnippets));
        }

        return result.build();
    }
}
