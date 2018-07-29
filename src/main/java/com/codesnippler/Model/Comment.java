package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.json.JsonObject;
import java.util.Collection;
import java.util.Date;


public class Comment extends JsonModel {
    @Id
    private String id;

    private String content;
    private String userId;
    private String snippetId;
    private Long upvotes;
    private Long downvotes;

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
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
