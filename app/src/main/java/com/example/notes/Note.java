package com.example.notes;

public class Note {

    private int id;
    private String ownerEmail;
    private String title;
    private String content;
    private long creationDate;
    private boolean favorite;
    private String tag;

    public Note() {
    }

    public Note(String ownerEmail, String title, String content, long creationDate, String tag) {
        this.ownerEmail = ownerEmail;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.favorite = false;
        this.tag = tag;
    }

    public Note(int id, String ownerEmail, String title, String content, long creationDate, boolean favorite, String tag) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.favorite = favorite;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return title == null ? "" : title;
    }
}