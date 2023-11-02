package com.example.musicplayerdemo.adapter;

import android.content.Context;

import com.example.musicplayerdemo.controller.MusicItem;

import java.util.ArrayList;
import java.util.List;

public class SourceAdapter extends MusicAdapter {
    public static final List<MusicItem> sourceList = new ArrayList<>();
    public SourceAdapter(Context context, int textViewResourceID) {
        super(context, textViewResourceID, sourceList);
    }
    @Override public List<MusicItem> getData() { return sourceList; }
    @Override public MusicItem get(int pos) { return sourceList.get(pos); }
    @Override public void clear() { super.clear(); sourceList.clear(); }
    @Override public void add(MusicItem item) { sourceList.add(item); }
    @Override public void remove(MusicItem item) { sourceList.remove(item); }

}
