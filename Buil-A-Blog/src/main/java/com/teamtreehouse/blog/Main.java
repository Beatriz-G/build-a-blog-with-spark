package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.NotFoundException;
import spark.ModelAndView;
import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.Request;
import com.teamtreehouse.blog.model.Comment;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.Spark.post;
import static spark.Spark.get;

public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {

        staticFileLocation("/public");

        BlogDao dao = new SimpleBlogDao();

        BlogEntry blogEntry1 = new BlogEntry("The best day I've ever had", "I went to the beach and tanned. Then I got pizza and drank an ice cold Coca Cola. Life is worth living.", "SweetiePie");
        BlogEntry blogEntry2 = new BlogEntry("The absolute worst day I’ve ever had", "My ice cream fell and then I tripped. Everyone laughed and I ran home. Now I have no ice cream.", "SweetiePie");
        BlogEntry blogEntry3 = new BlogEntry("That time at the mall", "I bought a lot of shoes, and found a nice cashmere sweater. I bought a sweet treat at the dining hall and then met up with some friends.", "SweetiePie");
        BlogEntry blogEntry4 = new BlogEntry("Dude, where’s my car?", "Someone stole my car and I had to walk 50 miles home! My feet are peeling and now I'm careless.", "SweetiePie");
        dao.addEntry(blogEntry1);
        dao.addEntry(blogEntry2);
        dao.addEntry(blogEntry3);
        dao.addEntry(blogEntry4);

        before("/entries", (req, res) -> {
            if (req.cookie("password") != null) {
                req.attribute("password", req.cookie("password"));
            }
        });

        // Before filter for edit page
        before("/entries/:slug/edit", (req, res) -> {
            if (req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req, "Make sure to sign in first! Thanks!");
                res.redirect("/password");
                halt();
            }
        });

        // Before filter for new entry page
        before("/entries/new", (req, res) -> {
            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req, "Make sure to sign in first! Thanks!");
                res.redirect("/password");
                halt();
            }
        });

        // index.hbs page
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        // password.hbs
        get ("/password", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        // Redirects user, sends error message if wrong password
        post ("/password", (req, res) -> {
            String password = req.queryParams("password");
            if (password.equals("admin")) {
                res.cookie("password", password);
                res.redirect("/");
            } else {
                setFlashMessage(req, "Wrong password, please try again!");
                res.redirect("/password");
            }
            return null;
        });

        // new.hbs page
        get("/entries/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        // New entry
        post("/entries", (req, res) -> {
            String title = req.queryParams("title");
            String content = req.queryParams("content");
            String author = req.queryParams("author");
            BlogEntry blogEntry = new BlogEntry(title, content, author);
            dao.addEntry(blogEntry);
            res.redirect("/"); // tried adding "/entries/" but didn't work
            return null;
        });

        // detail.hbs page
        get ("/entries/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("blogDetails", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        // edit.hbs page
        get("/entries/:slug/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("blogDetails", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        // Users can edit blog entry and save the information
        post("/entries/:slug/edit", (req, res) -> {
            String newTitle = req.queryParams("title");
            String newContent = req.queryParams("content");
            BlogEntry edit = dao.edit(req.params("slug"), newTitle, newContent);
            res.redirect("/entries/" + edit.getSlug());
            return null;
        });








        // Code for "comment" starts here

        // Users can send/save a comment
        post("/entries/:slug/comments", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            String author = req.queryParams("name");
            String content = req.queryParams("comment");
            Comment newComment = new Comment(author, content);
            blogEntry.addComment(newComment);
            res.redirect("/entries/" + req.params("slug"));
            return null;
        });





        /* Started off with the code below, wanted a comment to show if comment is successful or not
            Added "String slug = req.params(":slug");" thought it might be the slug that isn't working, needs more slug? (??)

        post("/entries/:slug/comments", (req, res) -> {
            String slug = req.params(":slug");
            BlogEntry blogDetails = dao.findEntryBySlug(req.params("slug"));
            String name = req.queryParams("name");
            String content = req.queryParams("comment");
            Comment additionalComment = new Comment(name, content);
            blogDetails.addComment(additionalComment);
            if (dao.addEntry(blogDetails)) {
            setFlashMessage(req, "Comment added successfully! Yay!");
            } else {
                setFlashMessage(req, "Failed comment upload.");
            }
            res.redirect("/entries/" + req.params("slug"));
            return null;
        });
        */



        /* Tried this without the extra slug or the if/else saying comment uploaded or not

        post("/entries/:slug/comments", (req, res) -> {
            BlogEntry blogDetails = dao.findEntryBySlug(req.params("slug"));
            String author = req.queryParams("name");
            String content = req.queryParams("comment");
            Comment comment = new Comment(author, content);
            res.redirect("/entries/" + req.params("slug"));
            return null;
        */












        // not-found.hbs
        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(
                    new ModelAndView(null, "not-found.hbs"));
            res.body(html);
        });
    }

    // FlashMessage
    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }
}