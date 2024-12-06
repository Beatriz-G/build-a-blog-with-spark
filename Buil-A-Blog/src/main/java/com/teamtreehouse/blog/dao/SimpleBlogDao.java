package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlogDao implements BlogDao {

    private List<BlogEntry> entryBlog;

    public SimpleBlogDao() {
        entryBlog = new ArrayList<>();
    }

    // addEntry method to store new blog entries
    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return entryBlog.add(blogEntry);
    }

    // findAllEntries method to retrieve new blog entries
    @Override
    public List<BlogEntry> findAllEntries() {
        return new ArrayList<>(entryBlog);
    }

    // findEntryBySlug to fetch a specific blog entry
    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return entryBlog.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public BlogEntry edit(String slug, String newTitle, String newContent) {
        BlogEntry blogDetail = findEntryBySlug(slug);
        if (blogDetail !=null) {
            blogDetail.setSlug(slug);
            blogDetail.setTitle(slug);
            blogDetail.setContent(newContent);
        }
        return blogDetail;
    }
}