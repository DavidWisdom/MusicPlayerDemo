package com.example.musicplayerdemo.adapter;

import static com.example.musicplayerdemo.adapter.AutoAdapter.autoList;
import static com.example.musicplayerdemo.adapter.LoveAdapter.loveList;
import static com.example.musicplayerdemo.activity.MainActivity.musicAdapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.musicplayerdemo.R;
import com.example.musicplayerdemo.controller.MusicItem;
import com.example.musicplayerdemo.db.DB;
import com.squareup.picasso.Picasso;

import java.util.List;

public abstract class MusicAdapter extends ArrayAdapter<MusicItem> {
    private final int resourceID;
    public MusicAdapter(Context context, int textViewResourceID, List<MusicItem> objects) {
        super(context, textViewResourceID, objects);
        resourceID = textViewResourceID;
    }

    private static class ViewHolder {
        private ImageView music_img;
        private TextView music_title;
        private ImageView love_img;
        private ImageView add;
        private ImageView remove;
        private boolean love;
    }

    @Override
    public View getView(int pos, View v, ViewGroup p) {
        MusicItem musicItem = getItem(pos);
        View view;
        ViewHolder par;
        if (v == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceID, null);
            par = new ViewHolder();
            par.music_img = view.findViewById(R.id.music_image);
            par.music_title = view.findViewById(R.id.music_title);
            par.love_img = view.findViewById(R.id.favorite);
            par.add = view.findViewById(R.id.add);
            par.remove = view.findViewById(R.id.remove);
            view.setTag(par);
        } else {
            view = v;
            par = (ViewHolder)view.getTag();
        }
        if (musicItem.title != null) par.music_title.setText(musicItem.title);
        Picasso.get()
                .load("https:" + musicItem.img_url)
                .into(par.music_img);
        ColorStateList colorStateList;
        if (DB.query(musicItem.title, musicItem.audio_url, musicItem.img_url)) {
            colorStateList = ColorStateList.valueOf(Color.RED);
            par.love = true;
        } else {
            int color = ContextCompat.getColor(getContext(), R.color.dark);
            colorStateList = ColorStateList.valueOf(color);
            par.love = false;
        }
        par.love_img.setImageTintList(colorStateList);
        par.love_img.setOnClickListener(par_view -> {
            par.love = !par.love;
            ColorStateList colorState;
            if (par.love) {
                colorState = ColorStateList.valueOf(Color.RED);
                DB.insert(musicItem.title, musicItem.audio_url, musicItem.img_url);
                loveList.add(musicItem);
                musicAdapter.notifyDataSetChanged();
            } else {
                int color = ContextCompat.getColor(getContext(), R.color.dark);
                colorState = ColorStateList.valueOf(color);
                DB.delete(musicItem.title, musicItem.audio_url, musicItem.img_url);
                loveList.remove(musicItem);
                musicAdapter.notifyDataSetChanged();
            }
            par.love_img.setImageTintList(colorState);
        });
        par.add.setOnClickListener(par_view -> {
            // 声明一个Handler对象
            Handler handler = new Handler();

            // 在需要改变颜色的地方设置新颜色
            ColorStateList newColorState = ColorStateList.valueOf(Color.RED);
            par.add.setImageTintList(newColorState);
            autoList.add(musicItem);
            musicAdapter.notifyDataSetChanged();
            // 延迟一段时间后恢复原来颜色
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int color = ContextCompat.getColor(getContext(), R.color.dark);
                    ColorStateList originalColorState = ColorStateList.valueOf(color);
                    par.add.setImageTintList(originalColorState);
                }
            }, 300);
        });
        par.remove.setOnClickListener(par_view -> {
            Handler handler = new Handler();
            ColorStateList newColorState = ColorStateList.valueOf(Color.RED);
            par.remove.setImageTintList(newColorState);
            autoList.remove(musicItem);
            musicAdapter.notifyDataSetChanged();
            handler.postDelayed((Runnable) () -> {
                int color = ContextCompat.getColor(getContext(), R.color.dark);
                ColorStateList originalColorState = ColorStateList.valueOf(color);
                par.remove.setImageTintList(originalColorState);
            }, 300);
        });
        return view;
    }
    public abstract List<MusicItem> getData();
    public abstract MusicItem get(int pos);
    public abstract void add(MusicItem item);
    public abstract void remove(MusicItem item);
}
