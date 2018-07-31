package com.codesnippler.Model;

import com.codesnippler.Utility.JsonUtility;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;


public class CodeSnippet extends JsonModel {
    private static List<String> hidden = Arrays.asList("upvoters", "downvoters", "savers");

    @Id
    private String snippetId;

    private String title;
    private String description;
    private String code;
    private String userId;
    private String languageId;
    private Long viewsCount;
    private Long upvotes;
    private HashMap<String, Boolean> upvoters;
    private Long downvotes;
    private HashMap<String, Boolean> downvoters;
    private Long savedCount;
    private HashMap<String, Boolean> savers;
    private LinkedList<String> comments;

    @Transient
    private Long popularityScore;

    @Transient
    private Boolean upvoted;

    @Transient
    private Boolean downvoted;

    @Transient
    private Boolean saved;


    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public CodeSnippet() {}

    public CodeSnippet(String title, String description, String code, String userId, String languageId, long viewsCount,
                       long upvotes, HashMap<String, Boolean> upvoters, long downvotes,
                       HashMap<String, Boolean> downvoters, long savedCount, Date date, LinkedList<String> comments,
                       HashMap<String, Boolean> savers) {
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
        this.savers = savers;
        this.popularityScore = null;
    }


    public CodeSnippet(String title, String description, String code, String userId, String languageId, Date date) {
        this.title = title;
        this.description = description;
        this.code = code;
        this.userId = userId;
        this.languageId = languageId;
        this.viewsCount = (long)0;
        this.upvotes = (long)0;
        this.upvoters = new HashMap<>();
        this.downvotes = (long)0;
        this.downvoters = new HashMap<>();
        this.savedCount = (long)0;
        this.createdDate = date;
        this.comments = new LinkedList<>();
        this.savers = new HashMap<>();
        this.popularityScore = null;
    }


    public Long getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(long savedCount) {
        this.savedCount = savedCount;
    }

    public void incrementSavedCount() {
        this.savedCount += 1;
    }

    public Long getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(long downvotes) {
        this.downvotes = downvotes;
    }

    public Long getUpvotes() {
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

    public Long getViewsCount() {
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
        return snippetId;
    }

    public HashMap<String, Boolean> getUpvoters() {
        return upvoters != null ? upvoters : new HashMap<>();
    }

    public void addToUpvoters(String userId) {
        upvoters = getUpvoters();
        upvoters.put(userId, true);
        upvotes += 1;
    }

    public void removeFromUpvoters(String userId) {
        upvoters = getUpvoters();
        upvoters.remove(userId);
        upvotes -= 1;
    }

    public HashMap<String, Boolean> getDownvoters() {
        return downvoters != null ? downvoters : new HashMap<>();
    }

    public void addToDownvoters(String userId) {
        downvoters = getDownvoters();
        downvoters.put(userId, true);
        downvotes += 1;
    }

    public void removeFromDownvoters(String userId) {
        downvoters = getDownvoters();
        downvoters.remove(userId);
        downvotes -= 1;
    }

    public LinkedList<String> getComments() {
        return comments != null ? comments : new LinkedList<>();
    }

    public void addToComments(String commentId) {
        this.comments = getComments();
        this.comments.addFirst(commentId);
    }

    public void removeFromComments(String commentId) {
        this.comments = getComments();
        comments.remove(commentId);
    }

    public HashMap<String, Boolean> getSavers() {
        return savers != null ? savers : new HashMap<>();
    }

    public void addToSavers(String userId) {
        savers = getSavers();
        savers.put(userId, true);
        incrementSavedCount();
    }

    public void removeFromSavers(String userId) {
        savers = getSavers();
        savers.remove(userId);
        savedCount -= 1;
    }

    public void setUpvoted(User user) {
        upvoters = getUpvoters();
        upvoted = upvoters.containsKey(user.getId());
    }

    public void setDownvoted(User user) {
        downvoters = getDownvoters();
        downvoted = downvoters.containsKey(user.getId());
    }

    public void setSaved(User user) {
        savers = getSavers();
        saved = savers.containsKey(user.getId());
    }

    public void setPopularityScore() {
        popularityScore = savedCount + viewsCount + upvotes - downvotes;
    }

    @Override
    public JsonObject toJson() {
        return super.toJson(hidden);
    }

    @Override
    public JsonObject toJson(Map<String, ?> addOns) {
        JsonObjectBuilder result = super.toJsonBuilder(hidden);
        result = JsonUtility.addJsonValues(result, addOns);
        return result.build();
    }

    @Override
    public JsonObjectBuilder toJsonBuilder(Map<String, ?> addOns) {
        JsonObjectBuilder result = super.toJsonBuilder(hidden);
        return JsonUtility.addJsonValues(result, addOns);
    }
}
