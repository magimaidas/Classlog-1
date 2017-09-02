package com.studio.classlog;

/**
 * Created by Studio on 8/27/2017.
 */

public class Blog {

    private String image, title, description, username;

    public Blog() {
    }

    public Blog(String image, String title, String description, String username) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.username = username;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
