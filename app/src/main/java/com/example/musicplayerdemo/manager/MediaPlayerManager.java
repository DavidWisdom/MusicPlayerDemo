package com.example.musicplayerdemo.manager;

import android.media.MediaPlayer;

import java.io.FileInputStream;
import java.io.IOException;

public class MediaPlayerManager {
    public static final Object lock = new Object();
    public static MediaPlayer mediaPlayer;

    public void playMedia(FileInputStream fis) throws IOException {
        synchronized (lock) {
            releaseMediaPlayer();
            MediaPlayer mediaPlayer2 = new MediaPlayer();
            mediaPlayer = mediaPlayer2;
            mediaPlayer2.setDataSource(fis.getFD());
            MediaPlayer mediaPlayer3 = mediaPlayer;
            if (mediaPlayer3 != null) {
                mediaPlayer3.setLooping(true);
            }
            MediaPlayer mediaPlayer4 = mediaPlayer;
            if (mediaPlayer4 != null) {
                mediaPlayer4.prepare();
            }
            MediaPlayer mediaPlayer5 = mediaPlayer;
            if (mediaPlayer5 != null) {
                mediaPlayer5.start();
            }
        }
    }

    public void releaseMediaPlayer() {
        MediaPlayer media = mediaPlayer;
        if (media != null) {
            if (media.isPlaying()) {
                media.stop();
            }
            media.release();
        }
        mediaPlayer = null;
    }
}
