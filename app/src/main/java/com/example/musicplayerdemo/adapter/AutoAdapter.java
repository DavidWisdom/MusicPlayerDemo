package com.example.musicplayerdemo.adapter;

import android.content.Context;

import com.example.musicplayerdemo.controller.MusicItem;

import java.util.ArrayList;
import java.util.List;

public class AutoAdapter extends MusicAdapter {
    public static final List<MusicItem> autoList = new ArrayList<>();
    public AutoAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID, autoList);
    }
    @Override
    public List<MusicItem> getData() {
        return autoList;
    }
    @Override
    public MusicItem get(int pos) {
        return autoList.get(pos);
    }
    @Override
    public void add(MusicItem item) {
        autoList.add(item);
    }
    @Override
    public void remove(MusicItem item) {
        autoList.remove(item);
    }
}
