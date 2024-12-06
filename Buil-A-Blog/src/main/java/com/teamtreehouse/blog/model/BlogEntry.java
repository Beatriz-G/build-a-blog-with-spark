package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BlogEntry {

    private String slug;
    private String title;
    private String content;
    private String author;
    private LocalDateTime date;
    private List<Comment> comments;

    public BlogEntry(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.date = LocalDateTime.now();
        this.comments = new ArrayList<>();
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        return date.format(formatter);
    }

    public String getSlug() {
        return slug;
    }

    // Methods that manage comments
    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSlug(String slug) {
        try{
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}