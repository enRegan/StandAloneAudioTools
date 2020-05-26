package com.regan.saata.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;

import com.regan.saata.Constant;
import com.regan.saata.bean.MediaInfo;
import com.regan.saata.bean.VideoInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {
    private static FileManager mInstance;
    private static Context mContext;
    private static ContentResolver mContentResolver;
    private static Object mLock = new Object();
    public static String DEVICES_ID_NAME = "config";

    public static FileManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new FileManager();
                    mContext = context;
                    mContentResolver = context.getContentResolver();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取本机音乐列表
     *
     * @return
     */
    public ArrayList<MediaInfo> getMusics(String sourcePath) {
        ArrayList<MediaInfo> musics = new ArrayList<>();
        Cursor c = null;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.YEAR,
                            MediaStore.Audio.Media.MIME_TYPE,
                            MediaStore.Audio.Media.SIZE,
                            MediaStore.Audio.Media.DATA}, null, null,
                    MediaStore.Audio.Media.DATE_MODIFIED + " DESC");
            while (c.moveToNext()) {
                try {
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                    if (!isExists(path)) {
                        continue;
                    }
                    if (!sourcePath.contains(sourcePath)) {
                        continue;
                    }

                    String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                    String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                    long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                    int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                    int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id

                    MediaInfo music = new MediaInfo();
                    music.setId(id);
                    music.setPath(path);
                    music.setName(name);
                    music.setSize(String.valueOf(size));
                    File file = new File(path);
                    FileInputStream fis = new FileInputStream(file);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.prepare();
                    long time = mediaPlayer.getDuration();//获得了文件的时长（以毫秒为单位）
                    music.setTime(String.valueOf(time));
                    music.setParams(name);
                    musics.add(music);
                    mediaPlayer.reset();
                } catch (Exception e) {
                    LogUtils.d(Constant.TAG, "fileManager Exception :" + e.getMessage());
                    continue;
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            LogUtils.d(Constant.TAG, "fileManager Exception :" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return musics;
    }

    MediaPlayer mediaPlayer;

    /**
     * 获取本机音乐列表
     *
     * @return
     */
    public ArrayList<MediaInfo> getMusics() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        ArrayList<MediaInfo> musics = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.YEAR,
                            MediaStore.Audio.Media.MIME_TYPE,
                            MediaStore.Audio.Media.SIZE,
                            MediaStore.Audio.Media.DATA}, null, null,
                    MediaStore.Audio.Media.DATE_MODIFIED + " DESC");
//            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
//                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//            context.getContentResolver().query(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    new String[] { MediaStore.Audio.Media._ID,
//                            MediaStore.Audio.Media.DISPLAY_NAME,
//                            MediaStore.Audio.Media.TITLE,
//                            MediaStore.Audio.Media.DURATION,
//                            MediaStore.Audio.Media.ARTIST,
//                            MediaStore.Audio.Media.ALBUM,
//                            MediaStore.Audio.Media.YEAR,
//                            MediaStore.Audio.Media.MIME_TYPE,
//                            MediaStore.Audio.Media.SIZE,
//                            MediaStore.Audio.Media.DATA },
//                    MediaStore.Audio.Media.MIME_TYPE + "=? or "
//                            + MediaStore.Audio.Media.MIME_TYPE + "=?",
//                    new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                if (!isExists(path)) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
//                int time = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));// 歌曲的id
                // int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                MediaInfo music = new MediaInfo();
                music.setId(id);
                music.setPath(path);
                music.setName(name);
                music.setSize(String.valueOf(size));
                long time = 0;
                try {
                    File file = new File(path);
                    FileInputStream fis = new FileInputStream(file);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.prepare();
                    time = mediaPlayer.getDuration();//获得了文件的时长（以毫秒为单位）
                } catch (IOException ioe) {
                    LogUtils.d(Constant.TAG, "getDuration : " + ioe.getMessage());
                    mediaPlayer.reset();
                    continue;
                }
                music.setTime(String.valueOf(time));
                music.setParams(name);
                musics.add(music);
                mediaPlayer.reset();
            }
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mediaPlayer = null;
            if (c != null) {
                c.close();
            }
        }
        return musics;
    }

    /**
     * 获取本机音乐列表
     *
     * @return
     */
    public ArrayList<VideoInfo> getVideo() {
        ArrayList<VideoInfo> videoInfos = new ArrayList<>();
        Cursor c = null;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.TITLE,
                            MediaStore.Video.Media.DURATION,
                            MediaStore.Video.Media.ARTIST,
                            MediaStore.Video.Media.ALBUM,
                            MediaStore.Video.Media.MIME_TYPE,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DATA}, null, null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC");
//            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
//                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//            context.getContentResolver().query(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    new String[] { MediaStore.Audio.Media._ID,
//                            MediaStore.Audio.Media.DISPLAY_NAME,
//                            MediaStore.Audio.Media.TITLE,
//                            MediaStore.Audio.Media.DURATION,
//                            MediaStore.Audio.Media.ARTIST,
//                            MediaStore.Audio.Media.ALBUM,
//                            MediaStore.Audio.Media.YEAR,
//                            MediaStore.Audio.Media.MIME_TYPE,
//                            MediaStore.Audio.Media.SIZE,
//                            MediaStore.Audio.Media.DATA },
//                    MediaStore.Audio.Media.MIME_TYPE + "=? or "
//                            + MediaStore.Audio.Media.MIME_TYPE + "=?",
//                    new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

//                if (!isExists(path)) {
//                    continue;
//                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
                String type = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));// 歌曲的id
//                int time = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));// 歌曲的id
                // int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setPath(path);
                videoInfo.setName(name);
                videoInfo.setSize(String.valueOf(size));
                File file = new File(path);
                FileInputStream fis = new FileInputStream(file);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                long time = mediaPlayer.getDuration();//获得了文件的时长（以毫秒为单位）
                videoInfo.setTime(String.valueOf(time));
                videoInfo.setParams(name);
                videoInfo.setType(type);
                videoInfos.add(videoInfo);
                mediaPlayer.reset();
            }
            mediaPlayer.release();
            mediaPlayer = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videoInfos;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 字符串保存到手机内存设备中
     *
     * @param str
     */
    public static void saveFile(String str, String fileName) {
        // 创建String对象保存文件名路径
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                // 创建指定路径的文件
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data", fileName);
                // 如果文件不存在
                if (file.exists()) {
                    // 创建新的空文件
                    file.delete();
                }
                file.createNewFile();
                // 获取文件的输出流对象
                FileOutputStream outStream = new FileOutputStream(file);
                // 获取字符串对象的byte数组并写入文件流
                outStream.write(str.getBytes());
                // 最后关闭文件输出流
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 删除已存储的文件
     */
    public static void deletefile(String fileName) {
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件里面的内容
     *
     * @return
     */
    public static String getFile(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                // 创建文件
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data", fileName);
                // 创建FileInputStream对象
                FileInputStream fis = new FileInputStream(file);
                // 创建字节数组 每次缓冲1M
                byte[] b = new byte[1024];
                int len = 0;// 一次读取1024字节大小，没有数据后返回-1.
                // 创建ByteArrayOutputStream对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 一次读取1024个字节，然后往字符输出流中写读取的字节数
                while ((len = fis.read(b)) != -1) {
                    baos.write(b, 0, len);
                }
                // 将读取的字节总数生成字节数组
                byte[] data = baos.toByteArray();
                // 关闭字节输出流
                baos.close();
                // 关闭文件输入流
                fis.close();
                // 返回字符串对象
                return new String(data);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
