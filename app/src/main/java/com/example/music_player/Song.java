package com.example.music_player;

import java.io.Serializable;

public class Song implements Serializable {
    private String name;
    private String artist;
    private String link;
    private String imageID;

    public Song(String name, String link,String artist, String imageID)
    {
        this.name = name;
        this.artist = artist;
        this.link = link;
        this.imageID = imageID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getimageID() {
        return imageID;
    }

    public void setimageID(String imageId) {
        this.imageID = imageID;
    }


}
