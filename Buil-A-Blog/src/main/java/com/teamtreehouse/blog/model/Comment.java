package com.teamtreehouse.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    private String author;
    private String content;
    private LocalDateTime date;

    // Comment class with author, content, and date fields
    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
        this.date = LocalDateTime.now();
    }

    public String getAuthor() {
        return author;
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
}