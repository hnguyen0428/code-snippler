package com.codesnippler.Model;

import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Utility.JsonUtility;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.*;


public class Comment extends JsonModel {
    private static List<String> hidden = Arrays.asList("upvoters", "downvoters");

    @Id
    private String commentId;

    private String content;
    private String userId;
    private String snippetId;
    private Long upvotes;
    private HashMap<String, Boolean> upvoters;
    private Long downvotes;
    private HashMap<String, Boolean> downvoters;

    @Transient
    private Boolean upvoted;

    @Transient
    private Boolean downvoted;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public Comment() {}

    public Comment(String content, String userId, String snippetId, Long upvotes, Long downvotes, Date createdDate) {
        this.content = content;
        this.userId = userId;
        this.snippetId = snippetId;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.createdDate = createdDate;
    }


    public Comment(String content, String userId, String snippetId, Date createdDate) {
        this.content = content;
        this.userId = userId;
        this.snippetId = snippetId;
        this.createdDate = createdDate;

        this.upvotes = (long)0;
        this.downvotes = (long)0;
        this.upvoted = null;
        this.downvoted = null;
    }

    public String getId() {
        return commentId;
    }

    public void setId(String id) {
        this.commentId = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSnippetId() {
        return snippetId;
    }

    public void setSnippetId(String snippetId) {
        this.snippetId = snippetId;
    }

    public Long getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(long upvotes) {
        this.upvotes = upvotes;
    }

    public Long getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(long downvotes) {
        this.downvotes = downvotes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public void setUpvoted(User user) {
        upvoters = getUpvoters();
        upvoted = upvoters.containsKey(user.getId());
    }

    public void setDownvoted(User user) {
        downvoters = getDownvoters();
        downvoted = downvoters.containsKey(user.getId());
    }

    public void includeUserDetails(UserRepository userRepo) {
        Optional<User> userOpt = userRepo.findById(this.getUserId());
        userOpt.ifPresent(user -> includeInJson("user", user));
    }

    public void setUserRelatedStatus(User user) {
        setUpvoted(user);
        setDownvoted(user);
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
    public JsonObjectBuilder toJsonBuilder() {
        return super.toJsonBuilder(hidden);
    }

    @Override
    public JsonObjectBuilder toJsonBuilder(Map<String, ?> addOns) {
        JsonObjectBuilder result = super.toJsonBuilder(hidden);
        return JsonUtility.addJsonValues(result, addOns);
    }
}
