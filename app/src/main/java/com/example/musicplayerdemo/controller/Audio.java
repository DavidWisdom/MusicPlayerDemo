package com.example.musicplayerdemo.controller;

import static com.example.musicplayerdemo.activity.MainActivity.python;

import android.content.Context;

import com.chaquo.python.PyObject;
import com.example.musicplayerdemo.activity.MainActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Audio {
    private static boolean contains(List<String> list, String value) {
        for (String item : list) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
    public static List<List<String>> search(String keyword) throws Exception {
        String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
        URL url = new URL("https://search.bilibili.com/all?keyword=" + encodedKeyword);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        connection.setRequestProperty("Referer", "https://www.bilibili.com");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        // 获取Content-Type中的charset参数
        String contentType = connection.getHeaderField("Content-Type");
        String charset = "utf-8"; // 默认编码为UTF-8
        if (contentType != null) {
            Pattern pattern = Pattern.compile("charset=([\\w-]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(contentType);
            if (matcher.find()) {
                charset = matcher.group(1); // 获取匹配到的charset参数值
            }
        }
        StringBuilder response = new StringBuilder();
        InputStream inputStream = connection.getInputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            assert charset != null;
            response.append(new String(buffer, 0, length, charset));
        }

        inputStream.close();
        reader.close();
        assert charset != null;
        String respText = new String(response.toString().getBytes(charset), charset);
        Pattern hrefRegex = Pattern.compile("a href=\"(.*?)\"");
        Matcher matcher = hrefRegex.matcher(respText);
        List<String> searchUrl = new ArrayList<>();
        while (matcher.find()) {
            String href = matcher.group(1);
            assert href != null;
            if (href.contains("video") && !contains(searchUrl, href)) {
                searchUrl.add(href);
            }
        }
        String respText2 = new String(response.toString().getBytes(charset), charset);
        Pattern titleRegex = Pattern.compile("class=\"bili-video-card__info--tit\" title=\"(.*?)\"");
        Matcher titleMatcher = titleRegex.matcher(respText2);
        List<String> titleList = new ArrayList<>();
        while (titleMatcher.find()) {
            String title = titleMatcher.group(1);
            assert title != null;
            titleList.add(title);
        }
        String respText3 = new String(response.toString().getBytes(charset), charset);
        Pattern imageRegex = Pattern.compile("webp\"><img src=\"(.*?)@672w_378h_1c_!web-search-common-cover\" ");
        Matcher imageMatcher = imageRegex.matcher(respText3);
        List<String> imageList = new ArrayList<>();
        while (imageMatcher.find()) {
            String image = imageMatcher.group(1);
            assert image != null;
            imageList.add(image);
        }
        List<List<String>> videoList = new ArrayList<>();
        videoList.add(titleList);
        videoList.add(searchUrl);
        videoList.add(imageList);
        return videoList;
    }
    public static String[] getAudioUrl(String video) {
        String[] result = new String[2];
        String url = "https:" + video;
        PyObject pyObject = python.getModule("main").get("say_hello");
        PyObject res = pyObject.call(url);
        result[0] = url;
        result[1] = res.toString();
        return result;
    }
    public static void getAudio(Context context, String title, String audio) throws Exception {
        URL audio_url = new URL(audio);
        HttpURLConnection conn = (HttpURLConnection) audio_url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37");
        conn.setRequestProperty("Referer", "https://www.bilibili.com");
        InputStream inputStream = conn.getInputStream();
        byte[] buffer = new byte[1024];
        int len;
        FileOutputStream fileOut;
        fileOut = context.openFileOutput(title + ".mp3", Context.MODE_PRIVATE);
        while ((len = inputStream.read(buffer)) != -1) {
            fileOut.write(buffer, 0, len);
        }
        fileOut.close();
        File file = new File(context.getFilesDir(), title + ".mp3");
        MainActivity.lastFile = file;
        MainActivity.mediaPlayerManager.playMedia(new FileInputStream(file));
    }
}
