package com.example.musicplayerdemo.db;

import static com.example.musicplayerdemo.adapter.LoveAdapter.loveList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicplayerdemo.controller.MusicItem;

public class DB {
    public static SQLiteDatabase db;
    public static void insert(String title, String audio_url, String image_url) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("audio_url", audio_url);
        values.put("image_url", image_url);
        db.insert("favorite", null, values);
    }
    public static void delete(String title, String audio_url, String image_url) {
        String selection = "title=? AND audio_url=? AND image_url=?";
        String[] selectionArgs = {title, audio_url, image_url};
        db.delete("favorite", selection, selectionArgs);
    }
    public static boolean query(String title, String audio_url, String image_url) {
        if (title == null || audio_url == null || image_url == null) return false;
        String selection = "title=? AND audio_url=? AND image_url=?";
        String[] selectionArgs = {title, audio_url, image_url};
        Cursor cursor = db.query("favorite", new String[]{"title", "audio_url", "image_url"}, selection, selectionArgs, null, null, "id", "1");
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    @SuppressLint("Range")
    public static void find() {
        Cursor cursor = db.query("favorite", new String[]{"title", "audio_url", "image_url"}, null, null, null, null, "id", null);
        String title;
        String audio_url;
        String image_url;
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
            audio_url = cursor.getString(cursor.getColumnIndex("audio_url"));
            image_url = cursor.getString(cursor.getColumnIndex("image_url"));
            loveList.add(new MusicItem(title, audio_url, image_url));
        }
        cursor.close();
    }
}
