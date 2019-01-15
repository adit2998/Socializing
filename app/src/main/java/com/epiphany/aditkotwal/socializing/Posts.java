package com.epiphany.aditkotwal.socializing;

public class Posts {
    public String uid, time, profileImage, postImage, name, description, date;

    public Posts() {

    }

    public Posts(String uid, String time, String profileImage, String postImage, String name, String description, String date) {
        this.uid = uid;
        this.time = time;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
