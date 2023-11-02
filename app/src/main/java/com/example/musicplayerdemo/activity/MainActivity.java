package com.example.musicplayerdemo.activity;



import static com.example.musicplayerdemo.controller.Audio.getAudio;
import static com.example.musicplayerdemo.controller.Audio.getAudioUrl;
import static com.example.musicplayerdemo.controller.Audio.search;
import static com.example.musicplayerdemo.manager.MediaPlayerManager.mediaPlayer;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;

//import android.support.v4.app.FragmentTransaction;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.musicplayerdemo.R;
//import com.example.musicplayerdemo.common.activities.SampleActivityBase;
//import com.example.musicplayerdemo.temp.activity.adapter.MusicAdapter;
import com.example.musicplayerdemo.adapter.AutoAdapter;
import com.example.musicplayerdemo.adapter.LoveAdapter;
import com.example.musicplayerdemo.adapter.MusicAdapter;
import com.example.musicplayerdemo.adapter.SourceAdapter;
import com.example.musicplayerdemo.controller.MusicItem;
import com.example.musicplayerdemo.db.DB;
import com.example.musicplayerdemo.db.DBOpenHelper;
import com.example.musicplayerdemo.manager.MediaPlayerManager;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    public static final MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();
    public static File lastFile = null;
    public static MusicAdapter musicAdapter;
    private static float rotateDegrees = 0f;
    private static boolean isRotate = false;
    private ListView listView;
    private EditText keywordView;
    private final TextView[] pageView = new TextView[3];
    private ImageView search;
    private int state = 0;
    private static boolean isCirclePlay = false;
    private MediaPlayer itemMediaPlayer;
    private List<MusicItem> itemsMedia;
    private int currentIndex = 0;
    public static Python python;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBOpenHelper dbSQLiteOpenHelper = new DBOpenHelper(MainActivity.this,"users.db",null,1);
        DB.db = dbSQLiteOpenHelper.getWritableDatabase();
        DB.find();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        python = Python.getInstance();
        itemMediaPlayer = new MediaPlayer();
        itemMediaPlayer.setOnCompletionListener(this);
        ImageView flush = findViewById(R.id.flush);
        flush.setOnClickListener(view -> {
            if (!isRotate) {
                isCirclePlay = !isCirclePlay;
                if (isCirclePlay) {
                    Toast.makeText(this, "切换为多曲循环模式", Toast.LENGTH_SHORT).show();
                    synchronized (MediaPlayerManager.lock) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                    }
                    itemsMedia = musicAdapter.getData();
                    if (itemsMedia.size() > 0) {
                        playMusic(itemsMedia.get(0));
                    }
                }
                else {
                    Toast.makeText(this, "切换为单曲循环模式", Toast.LENGTH_SHORT).show();
                    if (itemMediaPlayer != null && itemMediaPlayer.isPlaying()) {
                        itemMediaPlayer.stop();
                    }
                    if (lastFile != null) {
                        try {
                            MainActivity.mediaPlayerManager.playMedia(new FileInputStream(lastFile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                new Thread(() -> {
                    isRotate = true;
                    for (int i = 0; i < 90; ++i) {
                        rotateDegrees -= 2.0f;
                        flush.setRotation(rotateDegrees);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    rotateDegrees = 0f;
                    isRotate = false;
                }).start();
            }
        });
        keywordView = findViewById(R.id.keyword);
        search = findViewById(R.id.search);
        pageView[0] = findViewById(R.id.find);
        pageView[1] = findViewById(R.id.songs);
        pageView[2] = findViewById(R.id.auto);
        musicAdapter = new SourceAdapter(this, R.layout.music_item);
        listView = findViewById(R.id.listView);
        listView.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();
        search.setOnClickListener(view -> {
            setState(0);
            musicAdapter = new SourceAdapter(this, R.layout.music_item);
            listView.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
            String keyword = String.valueOf(keywordView.getText());
            Thread thread = new Thread(() -> {
                try {
                    musicAdapter.clear();
                    List<List<String>> videoList;
                    videoList = search(keyword);
                    List<String> titleList = videoList.get(0);
                    List<String> searchUrl = videoList.get(1);
                    List<String> imageUrl = videoList.get(2);
                    for (int i = 0; i < searchUrl.size(); ++i) {
                        musicAdapter.add(new MusicItem(titleList.get(i), searchUrl.get(i), imageUrl.get(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            try {
                thread.join();
                musicAdapter.notifyDataSetChanged();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (isCirclePlay) {
                Toast.makeText(this, "请先切换为单曲循环模式再播放具体歌曲", Toast.LENGTH_SHORT).show();
            } else {
                Thread thread = new Thread(() -> {
                    MusicItem item = musicAdapter.get(position);
                    if (item.audio_path != null) {
                        File file = new File(getFilesDir(), item.title + ".mp3");
                        System.out.println(item.title);
                        if (file.exists()) {
                            try {
                                mediaPlayerManager.playMedia(new FileInputStream(file));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                getAudio(getApplicationContext(), item.title, item.audio_path);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            String[] result = getAudioUrl(item.audio_url);
                            item.audio_path = result[1];
                            File file = new File(getFilesDir(), item.title + ".mp3");
                            if (file.exists()) {
                                try {
                                    mediaPlayerManager.playMedia(new FileInputStream(file));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    getAudio(getApplicationContext(), item.title, item.audio_path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        pageView[0].setTypeface(Typeface.DEFAULT_BOLD);
        pageView[0].setTextSize(18);
        pageView[0].setTextColor(getResources().getColor(R.color.orange));
        pageView[1].setTypeface(Typeface.DEFAULT);
        pageView[1].setTextSize(16);
        pageView[1].setTextColor(getResources().getColor(R.color.dark));
        pageView[2].setTypeface(Typeface.DEFAULT);
        pageView[2].setTextSize(16);
        pageView[2].setTextColor(getResources().getColor(R.color.dark));
        pageView[0].setOnClickListener(view -> {
            setState(0);
            musicAdapter = new SourceAdapter(getBaseContext(), R.layout.music_item);
            listView.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
        });
        pageView[1].setOnClickListener(view -> {
            setState(1);
            musicAdapter = new LoveAdapter(this, R.layout.music_item);
            listView.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
        });
        pageView[2].setOnClickListener(view -> {
            setState(2);
            musicAdapter = new AutoAdapter(this, R.layout.music_item);
            listView.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
        });
    }

    private void setState(int new_state) {
        if (state == new_state) return;
        pageView[state].setTypeface(Typeface.DEFAULT);
        pageView[state].setTextSize(16);
        pageView[state].setTextColor(getResources().getColor(R.color.dark));
        pageView[new_state].setTypeface(Typeface.DEFAULT_BOLD);
        pageView[new_state].setTextSize(18);
        pageView[new_state].setTextColor(getResources().getColor(R.color.orange));
        state = new_state;
    }
    private void playMusic(MusicItem item) {
        try {
            if (item.audio_path == null) {
                Thread thread = new Thread(() -> {
                    try {
                        String[] result = getAudioUrl(item.audio_url);
                        item.audio_path = result[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                thread.join();
            }
            File file = new File(getFilesDir(), item.title + ".mp3");
            if (file.exists()) {
                try {
                    if (itemMediaPlayer != null) {
                        if (itemMediaPlayer.isPlaying()) {
                            itemMediaPlayer.stop();
                        }
                        itemMediaPlayer.release();
                    }
                    lastFile = file;
                    itemMediaPlayer = new MediaPlayer();
                    itemMediaPlayer.setOnCompletionListener(this);
                    itemMediaPlayer.setDataSource(new FileInputStream(file).getFD());
                    itemMediaPlayer.prepare();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread t = new Thread(() -> {
                        String title = item.title;
                        String audio = item.audio_path;
                        try {
                            URL audio_url = new URL(audio);
                            HttpURLConnection conn;
                            conn = (HttpURLConnection) audio_url.openConnection();
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
                            conn.setRequestProperty("Referer", "https://www.bilibili.com");
                            InputStream inputStream = conn.getInputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            FileOutputStream fileOut;
                            fileOut = openFileOutput(title + ".mp3", Context.MODE_PRIVATE);
                            while ((len = inputStream.read(buffer)) != -1) {
                                fileOut.write(buffer, 0, len);
                            }
                            fileOut.close();
                            File file1 = new File(getFilesDir(), title + ".mp3");
                            lastFile = file1;
                            if (itemMediaPlayer != null) {
                                if (itemMediaPlayer.isPlaying()) {
                                    itemMediaPlayer.stop();
                                }
                                itemMediaPlayer.release();
                            }
                            itemMediaPlayer = new MediaPlayer();
                            itemMediaPlayer.setOnCompletionListener(this);
                            itemMediaPlayer.setDataSource(new FileInputStream(file1).getFD());
                            itemMediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            itemMediaPlayer.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        currentIndex++;
        if (currentIndex >= itemsMedia.size()) {
            currentIndex = 0;
        }
        if (currentIndex < itemsMedia.size()) {
            playMusic(itemsMedia.get(currentIndex));
        }
    }
}