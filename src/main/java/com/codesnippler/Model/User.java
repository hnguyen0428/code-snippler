package com.codesnippler.Model;

import com.codesnippler.Utility.JsonUtility;
import org.springframework.data.annotation.Id;
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

    public void addToSavedSnippets(String snippetId) {
        savedSnippets.put(snippetId, true);
    }

    public HashMap<String, Boolean> getCreatedSnippets() {
        return createdSnippets;
    }

    public void setCreatedSnippets(HashMap<String, Boolean> createdSnippets) {
        this.createdSnippets = createdSnippets;
    }

    public void addToCreatedSnippets(String snippetId) {
        this.createdSnippets.put(snippetId, true);
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public JsonObject toJson() {
        return super.toJson(hidden);
    }

    public String getId() {
        return id;
    }
}
