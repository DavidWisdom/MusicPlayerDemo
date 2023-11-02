package com.example.musicplayerdemo.adapter;

import android.content.Context;

import com.example.musicplayerdemo.controller.MusicItem;

import java.util.ArrayList;
import java.util.List;

public class LoveAdapter extends MusicAdapter {
    public static final List<MusicItem> loveList = new ArrayList<>();
    public LoveAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID, loveList);
    }
    @Override public List<MusicItem> getData() {
        return loveList;
    }
    @Override public MusicItem get(int pos) {
        return loveList.get(pos);
    }
    @Override public void add(MusicItem item) {
        loveList.add(item);
    }
    @Override public void remove(MusicItem item) {
        loveList.remove(item);
    }
}
