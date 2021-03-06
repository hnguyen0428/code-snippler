package com.codesnippler.Model;

import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Utility.JsonUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;


public class User extends JsonModel {
    public static List<String> hidden = Arrays.asList("password", "authenticated", "apiKey");

    @Id
    private String userId;

    @Indexed
    
    private String username;
    private String password;

    private String apiKey;
    private Map<String, Boolean> savedSnippets;
    private Map<String, Boolean> createdSnippets;

    private Map<String, String> profile;

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

    public Map<String, String> getProfile() {
        return profile;
    }

    public void updateProfile(String key, String value) {
        profile = profile != null ? profile : new HashMap<>();
        profile.put(key, value);
    }

    public void includeSavedSnippetsDetails(CodeSnippetRepository snippetRepo) {
        Set<String> snippetIds = this.getSavedSnippets().keySet();
        Iterable<CodeSnippet> itr = snippetRepo.findAllById(snippetIds);
        JsonArray snippetsArray = JsonUtility.listToJson(itr);
        includeInJson("savedSnippets", snippetsArray);
    }

    public void includeCreatedSnippetsDetails(CodeSnippetRepository snippetRepo) {
        Set<String> snippetIds = this.getCreatedSnippets().keySet();
        Iterable<CodeSnippet> itr = snippetRepo.findAllById(snippetIds);
        JsonArray snippetsArray = JsonUtility.listToJson(itr);
        includeInJson("createdSnippets", snippetsArray);
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
        if (!getAddOns().containsKey("savedSnippets"))
            json.add("savedSnippets", JsonUtility.listToJson(savedSnippets.keySet()));
        if (!getAddOns().containsKey("createdSnippets"))
            json.add("createdSnippets", JsonUtility.listToJson(createdSnippets.keySet()));

        return json;
    }

    @Override
    public JsonObjectBuilder toJsonBuilder(Map<String, ?> addOns) {
        JsonObjectBuilder result = super.toJsonBuilder(hidden);
        return JsonUtility.addJsonValues(result, addOns);
    }

    public String getId() {
        return userId;
    }
}
