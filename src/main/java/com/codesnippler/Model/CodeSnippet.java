package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.JsonObject;
import java.util.*;


public class CodeSnippet extends JsonModel {
    private static List<String> hidden = Arrays.asList("upvoters", "downvoters");

    @Id
    private String id;

    private String title;
    private String description;
    private String code;
    private String userId;
    private String languageId;
    private long viewsCount;
    private long upvotes;
    private HashMap<String, Boolean> upvoters;
    private long downvotes;
    private HashMap<String, Boolean> downvoters;
    private long savedCount;
    private List<String> comments;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public CodeSnippet() {}

    public CodeSnippet(String title, String description, String code, String userId, String languageId, long viewsCount,
                       long upvotes, HashMap<String, Boolean> upvoters, long downvotes,
                       HashMap<String, Boolean> downvoters, long savedCount, Date date, ArrayList<String> comments) {
        this.title = title;
        this.description = description;
        this.code = code;
        this.userId = userId;
        this.languageId = languageId;
        this.viewsCount = viewsCount;
        this.upvotes = upvotes;
        this.upvoters = upvoters;
        this.downvotes = downvotes;
        this.downvoters = downvoters;
        this.savedCount = savedCount;
        this.createdDate = date;
        this.comments = comments;
    }


    public CodeSnippet(String title, String description, String code, String userId, String languageId, Date date) {
        this.title = title;
        this.description = description;
        this.code = code;
        this.userId = userId;
        this.languageId = languageId;
        this.viewsCount = 0;
        this.upvotes = 0;
        this.upvoters = new HashMap<>();
        this.downvotes = 0;
        this.downvoters = new HashMap<>();
        this.savedCount = 0;
        this.createdDate = date;
        this.comments = new ArrayList<>();
    }


    public long getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(long savedCount) {
        this.savedCount = savedCount;
    }

    public void incrementSavedCount() {
        this.savedCount += 1;
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

    public void incrementViewsCount() {
        this.viewsCount += 1;
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

    public HashMap<String, Boolean> getUpvoters() {
        return upvoters != null ? upvoters : new HashMap<>();
    }

    public void addToUpvoters(String userId) {
        this.upvoters = this.upvoters != null ? this.upvoters : new HashMap<>();
        upvoters.put(userId, true);
        upvotes += 1;
    }

    public void removeFromUpvoters(String userId) {
        upvoters.remove(userId);
        upvotes -= 1;
    }

    public HashMap<String, Boolean> getDownvoters() {
        return downvoters != null ? downvoters : new HashMap<>();
    }

    public void addToDownvoters(String userId) {
        this.downvoters = this.downvoters != null ? this.downvoters : new HashMap<>();
        downvoters.put(userId, true);
        downvotes += 1;
    }

    public void removeFromDownvoters(String userId) {
        downvoters.remove(userId);
        downvotes -= 1;
    }

    @Override
    public JsonObject toJson() {
        return super.toJson(hidden);
    }

    public List<String> getComments() {
        return comments != null ? comments : new ArrayList<>();
    }

    public void addToComments(String commentId) {
        this.comments = this.comments != null ? this.comments : new ArrayList<>();
        this.comments.add(commentId);
    }
}
