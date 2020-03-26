package com.regan.saata.util;

import android.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileDurationUtil {

    public static int getDuration(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        int time = 0;
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            time = mediaPlayer.getDuration();//获得了文件的时长（以毫秒为单位）
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        return time;
    }
}
