package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashMap;


public class User {
    @Id
    private String id;

    private String username;
    private String password;
    private String apiKey;
    private HashMap<String, Boolean> savedSnippets;
    private HashMap<String, Boolean> createdSnippets;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public User(String username, String password, String apiKey, HashMap<String, Boolean> savedSnippets,
                HashMap<String, Boolean> createdSnippets, Date createdDate) {
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
        this.savedSnippets = savedSnippets;
        this.createdSnippets = createdSnippets;
        this.createdDate = createdDate;
    }

    public User(String username, String password, Date createdDate) {
        this.username = username;
        this.password = password;
        this.createdDate = createdDate;

        this.savedSnippets = new HashMap<>();
        this.createdSnippets = new HashMap<>();

        // TODO Encrypt password and generate random apiKey
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
}
