package com.codesnippler.Model;

import com.codesnippler.Utility.JsonUtility;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.constraints.NotNull;
import java.util.*;


public class User extends JsonModel {
    public static List<String> hidden = Arrays.asList("password");

    @Id
    private String id;

    private String username;
    private String password;

    private String apiKey;
    private Map<String, Boolean> savedSnippets;
    private Map<String, Boolean> createdSnippets;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    @Transient
    private boolean authenticated;

    public User() {}

    public User(String username, String password, String apiKey, LinkedHashMap<String, Boolean> savedSnippets,
                LinkedHashMap<String, Boolean> createdSnippets, Date createdDate) {
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

        this.savedSnippets = new LinkedHashMap<>();
        this.createdSnippets = new LinkedHashMap<>();
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

    public Map<String, Boolean> getSavedSnippets() {
        return savedSnippets != null ? savedSnippets : new LinkedHashMap<>();
    }

    public void addToSavedSnippets(String snippetId) {
        savedSnippets = getSavedSnippets();
        savedSnippets.put(snippetId, true);
    }

    public void removeFromSavedSnippets(String snippetId) {
        savedSnippets = getSavedSnippets();
        savedSnippets.remove(snippetId);
    }

    public Map<String, Boolean> getCreatedSnippets() {
        return createdSnippets != null ? createdSnippets : new LinkedHashMap<>();
    }

    public void addToCreatedSnippets(String snippetId) {
        createdSnippets = getCreatedSnippets();
        createdSnippets.put(snippetId, true);
    }

    public void removeFromCreatedSnippets(String snippetId) {
        createdSnippets = getCreatedSnippets();
        createdSnippets.remove(snippetId);
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setAuthenticated() {
        authenticated = true;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public JsonObject toJson() {
        return toJsonBuilder().build();
    }

    @Override
    public JsonObject toJson(Map<String, ?> addOns) {
        return toJsonBuilder(addOns).build();
    }

    @Override
    public JsonObjectBuilder toJsonBuilder() {
        JsonObjectBuilder json = super.toJsonBuilder(hidden);
        json.add("savedSnippets", JsonUtility.listToJson(savedSnippets.keySet()));
        json.add("createdSnippets", JsonUtility.listToJson(createdSnippets.keySet()));

        return json;
    }

    @Override
    public JsonObjectBuilder toJsonBuilder(Map<String, ?> addOns) {
        JsonObjectBuilder result = super.toJsonBuilder(hidden);
        return JsonUtility.addJsonValues(result, addOns);
    }

    public String getId() {
        return id;
    }
}
