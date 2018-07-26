package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;


public class CodeSnippet extends JsonModel {
    @Id
    private String id;

    private String title;
    private String description;
    private String code;
    private String userId;
    private String languageId;
    private long viewsCount;
    private long upvotes;
    private long downvotes;
    private long savedCount;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public CodeSnippet() {}

    public CodeSnippet(String title, String description, String code, String userId, String languageId, long viewsCount,
                       long upvotes, long downvotes, long savedCount, Date date) {
        this.title = title;
        this.description = description;
        this.code = code;
        this.userId = userId;
        this.languageId = languageId;
        this.viewsCount = viewsCount;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.savedCount = savedCount;
        this.createdDate = date;
    }


    public CodeSnippet(String title, String description, String code, String userId, String languageId, Date date) {
        this.title = title;
        this.description = description;
        this.code = code;
        this.userId = userId;
        this.languageId = languageId;
        this.viewsCount = 0;
        this.upvotes = 0;
        this.downvotes = 0;
        this.savedCount = 0;
        this.createdDate = date;
    }


    public long getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(long savedCount) {
        this.savedCount = savedCount;
    }

    public long getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(long downvotes) {
        this.downvotes = downvotes;
    }

    public long getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(long upvotes) {
        this.upvotes = upvotes;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }
}
