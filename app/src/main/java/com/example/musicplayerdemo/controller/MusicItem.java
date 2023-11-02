package com.example.musicplayerdemo.controller;

public class MusicItem {
    public String title;
    public String audio_url;
    public String img_url;
    public String audio_path;
    public MusicItem(String title, String audio_url, String image_url) {
        this.title = title;
        this.audio_url = audio_url;
        this.img_url = image_url;
        this.audio_path = null;
    }
}
