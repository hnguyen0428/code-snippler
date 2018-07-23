package com.codesnippler.Model;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;


public class Comment {
    @Id
    private String id;

    private String content;
    private String userId;
    private String snippetId;
    private long upvotes;
    private long downvotes;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate;

    public Comment(String content, String userId, String snippetId, long upvotes, long downvotes, Date createdDate) {
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

        this.upvotes = 0;
        this.downvotes = 0;
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

    public long getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(long upvotes) {
        this.upvotes = upvotes;
    }

    public long getDownvotes() {
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
